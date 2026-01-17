
|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-|
|       PROJEKT 3: System Zarządzania Czytelnią (Problem Czytelników i Pisarzy)         |
|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-|

AUTOR: Krzysztof Leśniak
GRUPA: Poniedziałek 18:30

1. OMÓWIENIE ALGORYTMU I KOMUNIKACJI MIĘDZY WĄTKAMI
--------------------------------------------------
Projekt implementuje klasyczny problem synchronizacji "Czytelnicy i Pisarze"
z ograniczonym zasobem (czytelnia mieści max 5 osób, pisarz ma wyłączność).

Algorytm:
- Wykorzystano mechanizm blokady sprawiedliwej (java.util.concurrent.locks.ReentrantLock
  z parametrem fair=true). Gwarantuje to, że wątki są obsługiwane w kolejności
  zgłoszenia (FIFO -> first in first out), co zapobiega zjawisku zagłodzenia pisarzy.
- Kolejka zewnętrzna jest reprezentowana przez obiekt LinkedList (waitingQ),
  który przechowuje identyfikatory wątków oczekujących na wejście.

Komunikacja:
- Synchronizacja opiera się na zmiennej warunkowej (Condition enter).
- Wątki wywołują metodę await(), gdy czytelnia jest zajęta lub gdy nie jest
  ich kolej (nie są na początku kolejki waitingQ).
- Każdy wątek opuszczający czytelnię wywołuje signalAll(), budząc wszystkie
  oczekujące wątki, aby sprawdziły, czy warunki wejścia są teraz spełnione.

2. SPOSÓB URUCHOMIENIA
----------------------
Program można uruchomić z linii komend przy użyciu skompilowanej paczki JAR:

Komenda: java -jar target/library-problem-1.0-SNAPSHOT.jar 10 3

W przypadku braku parametrów program domyślnie tworzy 10 czytelników i 3 pisarzy.

3. INFORMACJE O TESTACH I RAPORTACH
-----------------------------------
- Pokrycie kodu: Zapewniono prawie 100% pokrycia dla klas logicznych
  (Library, Reader, Writer) przy użyciu testów jednostkowych JUnit 5.
- Dokumentacja Javadoc
- Analiza Statyczna: Raport SonarQube (PDF) znajduje się w folderze sonar-cube.
  Zastosowano adnotację @SuppressWarnings("java:S106") w celu uciszenia sugestii o LOGGERACH
  oraz adnotację @SuppressWarnings("java:S2245"), aby uciszyć ostrzeżenia o braku
  bezpiecznego generatora liczb losowych.