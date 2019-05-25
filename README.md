# elbandito
Symulacja jednorękiego bandyty

Pseudo-aplikacja udostępnia 2 restowe serwisy w raz z odpowiednimi endpointami

Budowa/uruchomienie
  -

Budowa aplikacji: `mvn clean package`. Operacja tworzy wara o nazwie elbandito.
Kopiujemy wara do odpowiedniego miejsca w kontenerze webowym.
  
Aplikacja jest dostępna na: http://localhost:8080/elbandito 

Lub uruchamiamy swoje ulubione środowisko developerskie(Intellij Idea lub inne badziewie) i odpalamy jako Spring boot
  
Api swaggera: http://localhost:8080/elbandito/swagger-ui.html lub http://localhost:8080/swagger-ui.html

---

Kontrolery 
  -
* ### `BanditController`
  `/startGame` (GET) - rozpoczyna grę
  
  `/spin` (POST) - kręci walcami 
  
  `/endGame` (GET) - kończy grę
  
* ### `SessionController`
  `/sessions` (GET) - sprawdza aktualną sesję jednorękich bandytów
  
  `/finishAbandonedGames` (GET) - ustawia status końca gry(**END**), które są w statusie porzucone (**ABANDONED**)
  
  `/finishEndGames` (GET) - usuwa z sesji gry, które posiadają status **END**
  

* Konfiguracja aplikacji
  -
  
Lista tablic walców przypisana do jednorękich bandytów
W każdym walcu przypisane są konkretne symbole od 0 do 7.

    "reels": 
    [
         [0,1,2,3,4,4,4,5,6,7,7,0,0,2,2,3],
         [7,7,7,6,6,6,5,5,5,4,3,2,2,2,1,1,1,0,0,0,2,3,4,7],
         [0,1,0,2,0,3,4,5,6,6,6,6,6,5,5,5,1,0,1,2,3,4,1,1,1,0,0,7,7,7,5]
    ]
---       
Spiny - o ile symboli każdy walec musi się przekręcić.


    "spin": [6, 10, 14]
---    
Wygrana za takie same symbole w odpowiedniej linii    
Indeks tablicy przedstawia numer symbolu np: cyfra 3 = indeks 2 = numer symbolu w tablicy reels.
    
    "winnings": [1,12,3,2,5,10,99,1] 
---

Linie, które wygrywają
    
    "lines_winnings": 
    [
        [1,4,7],
        [2,4,8],
        [0,4,6],
        [6,4,8],
        [0,4,2]
    ]
    
Na przykład jednoręki bandynta wylosował:   

    2|1|1
    3|1|0
    4|1|1 
    
Aplikacja zamienia ww. 3 tablice czyli `[2,3,4]`,`[1,1,1]`,`[1,0,1]` na jedno-elementową tablice: `[2,3,4,1,1,1,1,0,1]`

Za pomocą listy tablic `lines_winnings` możemy określić wygrane ponieważ zawierają pozycję wygranych ww. jedno-elementowej tablicy czyli:
wygrana nastąpiła w tablicy: `[6,4,8]`. Tablica ta to nic innego jak pozycje takich samych symboli w `[2,3,4,1,1,1,1,0,1]` (proszę pamiętać, że indeks liczymy od 0).

Żeby uzmysłowić sobie linie wygrywające, które zostały zaprojektowane:

![Alt text](tablica_linie_wygrywajace.png?raw=true "Linie")