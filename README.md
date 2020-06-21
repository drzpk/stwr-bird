# stwr-bird - Dokumentacja

Niniejszy dokument zawiera dokumentację gry stwr-bird.

## Opis gry

Gra jest klonem popularnej gry *Flappy Bird*. Oprócz funkcjonalności obecnej w pierwowzorze zawiera także
dodatkowe elementy opisane w dalszej części dokumentu. Gra polega na sterowaniu tytułowym ptakiem tak, aby przelatywał
przez przeszkody nie dotykając ich. Sterowanie polega na stukaniu w ekran. Każde kliknięcie dodaje ptakowi chwilowy
odrzut w górę, dzięki czemu nie spadnie on na ziemię. Trzeba stukać w odpowiednich momentach, aby ptak znalazł się
na właściwej wysokości pozwalającej na przelecenie przez przeszkodę.

Gra wykorzystuje system punktacji. Za przelecenie przez jedną przeszkodę gracz dostaje jeden punkt. Gry z największą
punktacją zapisywane są na tablicy najlepszych wyników.

### Technologia wykorzystana przy tworzeniu gry

* Android SDK (wersja API 29)
* Android Studio
* Biblioteka [libGDX](https://libgdx.badlogicgames.com/)

### Funkcjonalność

* Sterowanie postacią w celu omijania przeszków
* Liczenie punktów za omijanie przeszkód i zapisywanie najlepszych wyników
* Możliwość grania jedną z dwóch grywalnych postaci
* Rosnący poziom trudności (różne typy przeszków)

## Opis elementów interfejsu

#### Menu główne 

Pierwszy ekran wyświetlany po uruchomieniu aplikacji. W prawym górnym rogu znajduje się przycisk pozwalający na
wybór postaci. Kliknięcie w dowolne inne miejsce rozpoczyna grę. 

![menu główne](docs/images/main_menu.png "Menu główne")

#### Ekran gry

Tutaj odbywa się właściwa rozgrywka. W centrum góry ekranu znajduje się licznik zdobytych to tej pory punktów.

![ekran gry](docs/images/game.png "Ekran gry")

#### Ekran końca gry

Ten ekran jest wyświetlany po zakończeniu gry. Prezentowane są uzyskane punkty. Po kliknięciu przycisku zielonej
strałki można ponownie rozpocząć grę. Po kliknięciu przycisku podium wyświetlana jest tablica najlepszych wyników.

![koniec gry](docs/images/game_over.png "Koniec gry")

## Kompilacja projektu

Po sklonowaniu projektu można go otworzyć w Androi Studio. Jednak w razie potrzeby plik *apk* można wygenerować bez
uruchamiania środowiska W tym celu należy uruchomić poniższe polecenia:

**Windows:**

```
gradlew.bat texturePacker assembleDebug
```

**Linux:**

```shell script
chmod +x gradlew
./gradlew taxturePacker assembleDebug
```

Po zakończeniu procesu budowania plik binarny gry znajzuje się w katalogu
`android/build/outputs/apk/debug`. Jeżeli na komputerze jest zainstalowany pakiet *ADB*, grę
można zainstalować na urządzeniu z Androidem za pomocą poniższego polecenia:

```
adb install android/build/outputs/apk/debug/android-debug.apk
``` 