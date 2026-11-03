ZenithOpus embedded internet session transport

The bundled Netty QUIC implementation is io.netty.incubator:
netty-incubator-codec-classes-quic:0.0.74.Final.e4mc.1, distributed under
the Apache License 2.0. Native binaries include Netty, Cloudflare Quiche
and BoringSSL; their license texts and the upstream notice accompany this file.
Native binaries are bundled and verified using the SHA-256 digests published
in that release's Quiche.java. No native downloads occur when using this feature.

The QUIClime protocol implementation is based on the public e4mc client/server
protocol. Attribution: Copyright (c) 2024 Skye, MIT License (e4mc-MIT.txt).
ZenithOpus implements its own bounded local TCP bridge, lifecycle, status UI,
native extraction and relay validation; it does not bundle or modify the e4mc mod.

Upstream projects:
https://github.com/vgskye/e4mc-minecraft-architectury
https://github.com/vgskye/e4mc-quiclime
https://github.com/vgskye/netty-incubator-codec-quic

Public session hosting relies on the independently operated e4mc relay service.
