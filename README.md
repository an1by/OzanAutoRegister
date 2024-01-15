# Autoreg Ozan

## For what?
This program was created by order of one of my clients for auto-registration and verification of accounts of the Turkish bank Ozan. It was required to insert a card into a new Spotify profile to receive a subscription to shares.

## How does the program work?
When using the library to test *Appium* applications and HTTP requests, the program shows the number on the 5SIM account with the balance, provides the code received from the bank in the program, the same process goes through for mail (DropMail is used). After this, the identity and cards are checked and their data is taken.

## Instructions:
1. Requires Andorid Studio and the included emulator with Ozan SuperApp pre-installed.
2. Requires pre-installed JDK (11+), NVM (1.1.12+) and Node.JS (16.14.0+).
3. Install Appium (application testing tool):
```shell
npm i --location=global application
installing the Appium uiautomator driver
```
4. Open `config.yml` and insert the API code with **5SIM**.
5. Open `passports.txt` and enter data lines from the database you have there;\
   **Example line:** `YAD000001 | 00000001 | 00000000001 | ZEYNEB | QASIM | 28.2.1980 | GAZIANTEP | SHEHITKAMIL | FATMA | No | AKHMET | No | SY`;\
   **The line sections used in the program are:** 3 - 6.
5. To start the server, enter the command: `appium server -pa /wd/hub -p 5723`.
6. To run the script, enter the command in a separate window: `java -jar .\OzanAutoRegister-1.0-SNAPSHOT-all.jar`.
7. Enjoy the process and get the card data in `saved_cards.txt`;
   **Format:** `CARD_NUMBER:MM:YY:CVU`.

All pre-prepared files are located in [default_assets folder](./default_assets).