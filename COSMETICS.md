# Встроенный пак косметики (Wardrobe)

В проект вшит полный серверный пак гардероба Zenith: **680 моделей** и **680 текстур**
(плащи, персонажи, одежда, питомцы, украшения, оружие/предметы, часы).

## Где лежит

```
src/main/resources/assets/zenith/wardrobe/
├── registry.json          # каталог 680 предметов (слоты, редкости, темы)
├── models/<id>.json       # геометрия/анимации модели
└── textures/<id>.png      # текстура модели
```

`id` совпадает с полем `model` в `registry.json`, например:
`watches/rolex_daytona`, `capes/angel`, `pets/ember_fox`, `characters/samurai_wanderer`.

## Как грузится

`org.zenith.utility.api.Index2#method6140` сначала ищет модель в classpath:

```
/assets/zenith/wardrobe/models/<id>.json
/assets/zenith/wardrobe/textures/<id>.png
```

Если оба файла найдены — возвращает их сразу, без сети и без ключей.
Если нет (например, модель новая и её ещё нет в паке) — работает старый путь:
локальная папка `-Dzenith.wardrobe.src`, затем удалённый индекс/блобы/ключи API.

Поэтому раздел Wardrobe полностью работает офлайн, а новые серверные модели
подхватываются как раньше.

## Сборка / запуск

```bat
gradlew.bat build
gradlew.bat runClient
```

Требуется JDK 21. Gradle-обёртка лежит в `gradle/wrapper` (Gradle 9.5.1).
