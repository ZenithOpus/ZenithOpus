$ErrorActionPreference = 'SilentlyContinue'
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Add-Type -AssemblyName System.Runtime.WindowsRuntime
$asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {
    $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and
    $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]

function Await($operation, [Type]$type, [int]$timeout = 4000) {
    $task = $asTask.MakeGenericMethod($type).Invoke($null, @($operation))
    if (-not $task.Wait($timeout)) { return $null }
    return $task.Result
}

[Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType = WindowsRuntime] | Out-Null
[Windows.Media.MediaPlaybackAutoRepeatMode, Windows.Media, ContentType = WindowsRuntime] | Out-Null
$script:manager = $null
$script:managerTask = $null
$script:managerAt = [DateTime]::MinValue
$managerType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]

function Get-Manager {
    if ($script:manager -ne $null) { return $script:manager }
    if ($script:managerTask -ne $null -and $script:managerTask.IsCompleted) {
        if (-not $script:managerTask.IsFaulted) { $script:manager = $script:managerTask.Result }
        $script:managerTask = $null
        if ($script:manager -ne $null) { return $script:manager }
    }
    if ($script:managerTask -eq $null -or ([DateTime]::UtcNow - $script:managerAt).TotalSeconds -gt 20) {
        $script:managerTask = $asTask.MakeGenericMethod($managerType).Invoke($null, @($managerType::RequestAsync()))
        $script:managerAt = [DateTime]::UtcNow
        $script:managerTask.Wait(1500) | Out-Null
        return (Get-ManagerReady)
    }
    return $null
}

function Get-ManagerReady {
    if ($script:managerTask -ne $null -and $script:managerTask.IsCompleted -and -not $script:managerTask.IsFaulted) {
        $script:manager = $script:managerTask.Result
        $script:managerTask = $null
    }
    return $script:manager
}

function Find-Session($owner) {
    $current = Get-Manager
    if ($current -eq $null) { return $null }
    foreach ($session in $current.GetSessions()) {
        if ($session.SourceAppUserModelId -eq $owner) { return $session }
    }
    return $current.GetCurrentSession()
}

Add-Type -TypeDefinition @"
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.InteropServices;
using System.Threading;

[ComImport, Guid("BCDE0395-E52F-467C-8E3D-C4579291692E")]
class ZenithDeviceEnumerator {}

[Guid("A95664D2-9614-4F35-A746-DE8DB63617E6"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
interface IZenithDeviceEnumerator {
    int EnumAudioEndpoints();
    [PreserveSig] int GetDefaultAudioEndpoint(int flow, int role, out IZenithDevice device);
}

[Guid("D666063F-1587-4E43-81F1-B948E807363F"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
interface IZenithDevice {
    [PreserveSig] int Activate(ref Guid iid, int context, IntPtr parameters, [MarshalAs(UnmanagedType.IUnknown)] out object result);
}

[Guid("77AA99A0-1BD6-484F-8BC7-2C654C9A9B6F"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
interface IZenithSessionManager {
    int GetAudioSessionControl();
    int GetSimpleAudioVolume();
    [PreserveSig] int GetSessionEnumerator(out IZenithSessionEnumerator sessions);
}

[Guid("E2F5BB11-0570-40CA-ACDD-3AA01277DEE8"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
interface IZenithSessionEnumerator {
    [PreserveSig] int GetCount(out int count);
    [PreserveSig] int GetSession(int index, out IZenithSessionControl session);
}

[Guid("bfb7ff88-7239-4fc9-8fa2-07c950be9c6d"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
interface IZenithSessionControl {
    [PreserveSig] int GetState(out int state);
    int GetDisplayName();
    int SetDisplayName();
    int GetIconPath();
    int SetIconPath();
    int GetGroupingParam();
    int SetGroupingParam();
    int RegisterAudioSessionNotification();
    int UnregisterAudioSessionNotification();
    [PreserveSig] int GetSessionIdentifier([MarshalAs(UnmanagedType.LPWStr)] out string id);
    [PreserveSig] int GetSessionInstanceIdentifier([MarshalAs(UnmanagedType.LPWStr)] out string id);
    [PreserveSig] int GetProcessId(out int pid);
}

[Guid("87CE5498-68D6-44E5-9215-6DA47EF883D8"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
interface IZenithSimpleVolume {
    [PreserveSig] int SetMasterVolume(float level, ref Guid context);
    [PreserveSig] int GetMasterVolume(out float level);
}

[Guid("C02216F6-8C67-4B5B-9D00-D008E73E0064"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
interface IZenithMeter {
    [PreserveSig] int GetPeakValue(out float peak);
}

public static class ZenithAudio {
    public static volatile string MeterOwner = "";
    static int fadeGeneration;

    public static void CancelFade() {
        Interlocked.Increment(ref fadeGeneration);
    }

    public static void Fade(string owner, float target, int millis) {
        int generation = Interlocked.Increment(ref fadeGeneration);
        var thread = new Thread(() => {
            try {
                float start = Get(owner);
                if (start < 0f) return;
                int steps = Math.Max(1, millis / 16);
                for (int i = 1; i <= steps; i++) {
                    if (generation != fadeGeneration) return;
                    float t = (float)i / steps;
                    float eased = t * t * (3f - 2f * t);
                    Set(owner, start + (target - start) * eased);
                    Thread.Sleep(16);
                }
            } catch {}
        });
        thread.IsBackground = true;
        thread.SetApartmentState(ApartmentState.MTA);
        thread.Start();
    }
    static Thread meter;

    public static void StartMeter(string owner) {
        MeterOwner = owner ?? "";
        if (meter != null) return;
        meter = new Thread(MeterLoop);
        meter.IsBackground = true;
        meter.SetApartmentState(ApartmentState.MTA);
        meter.Start();
    }

    static void MeterLoop() {
        var meters = new List<IZenithMeter>();
        string current = null;
        DateTime refresh = DateTime.MinValue;
        while (true) {
            try {
                string key = Key(MeterOwner);
                if (key.Length == 0) {
                    Thread.Sleep(250);
                    continue;
                }
                if (key != current || DateTime.UtcNow > refresh) {
                    meters.Clear();
                    current = key;
                    refresh = DateTime.UtcNow.AddSeconds(3);
                    var sessions = Sessions();
                    int count;
                    sessions.GetCount(out count);
                    for (int i = 0; i < count; i++) {
                        IZenithSessionControl control;
                        sessions.GetSession(i, out control);
                        if (Matches(control, key)) meters.Add((IZenithMeter)control);
                    }
                }
                float peak = 0f;
                foreach (var item in meters) {
                    float value;
                    if (item.GetPeakValue(out value) == 0 && value > peak) peak = value;
                }
                Console.Out.WriteLine("peak " + peak.ToString("0.0000", CultureInfo.InvariantCulture));
                Console.Out.Flush();
            } catch {
                meters.Clear();
                refresh = DateTime.MinValue;
            }
            Thread.Sleep(33);
        }
    }

    static string Key(string owner) {
        string key = (owner ?? "").ToLowerInvariant();
        int bang = key.IndexOf('!');
        if (bang >= 0) key = key.Substring(0, bang);
        int under = key.IndexOf('_');
        if (under > 0) key = key.Substring(0, under);
        return key;
    }

    static bool Matches(IZenithSessionControl control, string key) {
        string id;
        control.GetSessionInstanceIdentifier(out id);
        id = (id ?? "").ToLowerInvariant();
        if (key.Length > 0 && id.Contains(key)) return true;
        int pid;
        control.GetProcessId(out pid);
        if (pid == 0) return false;
        try {
            string name = Process.GetProcessById(pid).ProcessName.ToLowerInvariant();
            return name.Length > 2 && (key.Contains(name) || name.Contains(key.Replace(".exe", "")));
        } catch { return false; }
    }

    static IZenithSessionEnumerator Sessions() {
        var enumerator = (IZenithDeviceEnumerator)(new ZenithDeviceEnumerator());
        IZenithDevice device;
        enumerator.GetDefaultAudioEndpoint(0, 1, out device);
        Guid iid = typeof(IZenithSessionManager).GUID;
        object manager;
        device.Activate(ref iid, 23, IntPtr.Zero, out manager);
        IZenithSessionEnumerator sessions;
        ((IZenithSessionManager)manager).GetSessionEnumerator(out sessions);
        return sessions;
    }

    public static string List() {
        var sessions = Sessions();
        int count;
        sessions.GetCount(out count);
        var text = new System.Text.StringBuilder();
        for (int i = 0; i < count; i++) {
            IZenithSessionControl control;
            sessions.GetSession(i, out control);
            string id;
            control.GetSessionInstanceIdentifier(out id);
            int pid;
            control.GetProcessId(out pid);
            string name = "";
            try { name = Process.GetProcessById(pid).ProcessName; } catch {}
            text.Append(pid).Append(':').Append(name).Append(':').Append(id).Append(" ;; ");
        }
        return text.ToString();
    }

    public static float Get(string owner) {
        string key = Key(owner);
        var sessions = Sessions();
        int count;
        sessions.GetCount(out count);
        float fallback = -1f;
        for (int i = 0; i < count; i++) {
            IZenithSessionControl control;
            sessions.GetSession(i, out control);
            if (Matches(control, key)) {
                float level;
                ((IZenithSimpleVolume)control).GetMasterVolume(out level);
                int state;
                control.GetState(out state);
                if (state == 1) return level;
                if (fallback < 0f) fallback = level;
            }
        }
        return fallback;
    }

    public static bool Set(string owner, float value) {
        string key = Key(owner);
        var sessions = Sessions();
        int count;
        bool any = false;
        sessions.GetCount(out count);
        Guid none = Guid.Empty;
        for (int i = 0; i < count; i++) {
            IZenithSessionControl control;
            sessions.GetSession(i, out control);
            if (Matches(control, key)) {
                ((IZenithSimpleVolume)control).SetMasterVolume(Math.Max(0f, Math.Min(1f, value)), ref none);
                any = true;
            }
        }
        return any;
    }
}
"@

Get-Manager | Out-Null
[Console]::SetOut([System.IO.TextWriter]::Synchronized([Console]::Out))
[Console]::Out.WriteLine('ready')
[Console]::Out.Flush()

while ($true) {
    $line = [Console]::In.ReadLine()
    if ($line -eq $null) { break }
    $parts = $line.Split('|')
    try {
        switch ($parts[0]) {
            'seek' {
                $session = Find-Session $parts[1]
                $ticks = [long]([double]::Parse($parts[2], [Globalization.CultureInfo]::InvariantCulture) * 10000000)
                Await ($session.TryChangePlaybackPositionAsync($ticks)) ([bool]) | Out-Null
                [Console]::Out.WriteLine('seek ok')
            }
            'fade' {
                $value = [float]::Parse($parts[2], [Globalization.CultureInfo]::InvariantCulture)
                [ZenithAudio]::Fade($parts[1], $value, [int]$parts[3])
            }
            'vol' {
                $value = [float]::Parse($parts[2], [Globalization.CultureInfo]::InvariantCulture)
                [ZenithAudio]::CancelFade()
                [ZenithAudio]::Set($parts[1], $value) | Out-Null
            }
            'getvol' {
                $value = [ZenithAudio]::Get($parts[1])
                [Console]::Out.WriteLine('vol ' + $value.ToString([Globalization.CultureInfo]::InvariantCulture))
            }
            'shuffle' {
                $session = Find-Session $parts[1]
                $info = $session.GetPlaybackInfo()
                $next = -not [bool]$info.IsShuffleActive
                Await ($session.TryChangeShuffleActiveAsync($next)) ([bool]) | Out-Null
                [Console]::Out.WriteLine('state ' + $next + ' ' + [int]$info.AutoRepeatMode + ' ' + [bool]$info.Controls.IsShuffleEnabled + ' ' + [bool]$info.Controls.IsRepeatEnabled)
            }
            'repeat' {
                $session = Find-Session $parts[1]
                $info = $session.GetPlaybackInfo()
                $mode = [int]$info.AutoRepeatMode
                $next = if ($mode -eq 0) { 2 } elseif ($mode -eq 2) { 1 } else { 0 }
                Await ($session.TryChangeAutoRepeatModeAsync([Windows.Media.MediaPlaybackAutoRepeatMode]$next)) ([bool]) | Out-Null
                [Console]::Out.WriteLine('state ' + [bool]$info.IsShuffleActive + ' ' + $next + ' ' + [bool]$info.Controls.IsShuffleEnabled + ' ' + [bool]$info.Controls.IsRepeatEnabled)
            }
            'time' {
                $session = $null
                $current = Get-Manager
                if ($current -ne $null) {
                    foreach ($candidate in $current.GetSessions()) {
                        if ($candidate.SourceAppUserModelId -eq $parts[1]) { $session = $candidate }
                    }
                }
                if ($session -ne $null) {
                    $timeline = $session.GetTimelineProperties()
                    $status = [int]$session.GetPlaybackInfo().PlaybackStatus
                    $updated = 0
                    if ($timeline.LastUpdatedTime.Year -gt 1700) { $updated = $timeline.LastUpdatedTime.ToUnixTimeMilliseconds() }
                    $culture = [Globalization.CultureInfo]::InvariantCulture
                    [Console]::Out.WriteLine('time ' + $timeline.Position.TotalSeconds.ToString($culture) + ' ' +
                        ($timeline.EndTime - $timeline.StartTime).TotalSeconds.ToString($culture) + ' ' + $updated + ' ' + $status)
                }
            }
            'meter' {
                [ZenithAudio]::StartMeter($parts[1])
            }
            'audio' {
                [Console]::Out.WriteLine('audio ' + [ZenithAudio]::List())
            }
            'state' {
                $session = Find-Session $parts[1]
                $info = $session.GetPlaybackInfo()
                [Console]::Out.WriteLine('state ' + [bool]$info.IsShuffleActive + ' ' + [int]$info.AutoRepeatMode + ' ' +
                    [bool]$info.Controls.IsShuffleEnabled + ' ' + [bool]$info.Controls.IsRepeatEnabled)
            }
        }
    } catch {
        [Console]::Out.WriteLine('err')
    }
    [Console]::Out.Flush()
}
