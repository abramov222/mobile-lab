# Лабораторная работа №6. Жизненный цикл. Архитектурные компоненты

Абрамов Илья, ФИТ-241

| Задание | Проект | Что сделано |
|---|---|---|
| Контрольное задание 1 | `ActivityLifecycle` | Логирование всех методов жизненного цикла `MainActivity` (запуск, сворачивание, поворот экрана) |
| Контрольное задание 2 | `ArtSpace` | Приложение из лабораторной 5 переведено на `ArtSpaceViewModel` с `ArtSpaceUiState` и `StateFlow` |
| Контрольное задание 3 | `DessertClicker` | Состояние перенесено в `DessertUiState`, логика в `DessertViewModel` |
| Практическая часть и контрольное задание 4 | `Unscramble` | Игра на `GameViewModel` с `StateFlow`, unit-тесты `GameViewModelTest` (путь успеха, путь ошибки, граничные случаи) |

## Запуск

Открыть папку нужного проекта в Android Studio, подключить телефон по USB с включённой отладкой и нажать Run.

Тесты Unscramble:

```
gradlew testDebugUnitTest
```

## Скриншоты

| Activity Lifecycle | Activity Lifecycle (поворот) |
|---|---|
| ![](screenshots/lab6_1.png) | ![](screenshots/lab6_2.png) |

| Art Space | Dessert Clicker |
|---|---|
| ![](screenshots/lab6_3.png) | ![](screenshots/lab6_4.png) |

| Unscramble | Unscramble (неверный ответ) | Unscramble (конец игры) |
|---|---|---|
| ![](screenshots/lab6_5.png) | ![](screenshots/lab6_6.png) | ![](screenshots/lab6_8.png) |

| Unscramble (после поворота) |
|---|
| ![](screenshots/lab6_7.png) |

| Покрытие тестами |
|---|
| ![](screenshots/lab6_coverage.png) |
