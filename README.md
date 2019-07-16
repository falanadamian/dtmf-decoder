# DTMF Decoder

Aplikacja mobilna służąca do dekodowania tonów DTMF.

## Co to jest DTMF?

DTMF (Dual-Tone Multi-Frequency) jest to kod dźwiękowy, składający się z dwóch dźwięków. Powstaje w momencie naciśnięcia w telefonie przycisku (od 0 do 9 lub * i #) w wyniku nałożenia na siebie dwóch sinusoidalnych fal dźwiękowych o częstotliwościach przypisanych danemu przyciskowi.

### Schemat działania aplikacji

* Pobranie dźwięku z mikrofonu
* Przetworzenie pobranego dźwięku z użyciem Szybkiej Transformaty Fouriera
* Implementacja prostych algorytmów umożliwiających odfiltrowanie szumów
* Sprawdzenie czy widmo częstotliwościowe sygnału zawiera obydwie z poszukiwanych częstotliwości

## Szybka transformata Fouriera

Wejściem FFT jest wektor zespolony- jako część rzeczywistą przesyłane są dane rejestrowane przez mikrofon, natomiast część urojoną stanowi wektor zerowy. Na wyjściu FFT otrzymywana jest tablica liczb zespolonych. Następnie obliczana jest wartość absolutna z wyjściowej tablicy i wykreślane widmo częstotliwościowe sygnału.

Próbkowanie sygnału rejestrowanego przez mikrofon odbywa się z częstotliwością 16kHZ. Rozmiar bufora do którego dane te są zapisywane jest równy 1024. W związku z tym „zasięg” częstotliwości równy 8kHz jest równo dzielony na 512 części, każda z nich jest szeroka na 8000/512 = 15,625Hz - taka jest wartość rozdzielczości częstotliwości.

## Nagranie pokazujące działanie aplikacji

[DTMF decoder](https://youtu.be/Ahlnog24K3k)

## Zrzuty ekranu aplikacji

![Screenshot_20180522-161645](https://user-images.githubusercontent.com/52956982/61288920-76cdb000-a7c8-11e9-9381-73adc17aeae1.png)
![Screenshot_20180522-161657](https://user-images.githubusercontent.com/52956982/61288921-76cdb000-a7c8-11e9-8318-63daa55bec15.png)
![Screenshot_20180522-161709](https://user-images.githubusercontent.com/52956982/61288923-77664680-a7c8-11e9-87ef-90f89ed6a515.png)
![Screenshot_20180522-161718](https://user-images.githubusercontent.com/52956982/61288924-77664680-a7c8-11e9-931b-44d567aa2691.png)
![Screenshot_20180522-161727](https://user-images.githubusercontent.com/52956982/61288925-77664680-a7c8-11e9-9716-568c0aa69afe.png)
