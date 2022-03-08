import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class Kasjer implements KasjerInterface {

    RozmieniaczInterface rozmieniacz;
    List<Pieniadz> kasa = new LinkedList<>();

    public int policzRozmienialne(List<Pieniadz> pieniadze) {
        int wynik = 0;

        for (Pieniadz pieniadz : pieniadze) {
            if (pieniadz.czyMozeBycRozmieniony())
                wynik += pieniadz.wartosc();
        }
        return wynik;
    }

    public int policzNieRozmienialne(List<Pieniadz> pieniadze) {
        int wynik = 0;

        for (Pieniadz pieniadz : pieniadze) {
            if (!pieniadz.czyMozeBycRozmieniony())
                wynik += pieniadz.wartosc();
        }
        return wynik;
    }

    @Override
    public List<Pieniadz> rozlicz(int cena, List<Pieniadz> pieniadze) {
        List<Pieniadz> wynik = new ArrayList<>();
        List<Pieniadz> otrzymane = new ArrayList<>(pieniadze);

        int rozmienialne = policzRozmienialne(otrzymane);
        int nie_rozmienialne = policzNieRozmienialne(otrzymane);
        int wszystkie = rozmienialne + nie_rozmienialne;

        if (cena == wszystkie) {
            kasa.addAll(otrzymane);
            return wynik;
        }

        int reszta = wszystkie - cena;

        if (reszta <= rozmienialne) {
            while (true) {
                List<Pieniadz> rozmienione = new ArrayList<>();
                boolean check = true;

                for (Iterator<Pieniadz> it = otrzymane.iterator(); it.hasNext(); ) {
                    Pieniadz pieniadz = it.next();
                    if (pieniadz.czyMozeBycRozmieniony() && pieniadz.wartosc() != 1) {
                        rozmienione = rozmieniacz.rozmien(pieniadz);
                        it.remove();
                        check = false;
                        break;
                    }
                }

                otrzymane.addAll(rozmienione);

                if (check) {
                    break;
                }
            }
            for (int i = 0; i < reszta; ++i) {
                for (Iterator<Pieniadz> it = otrzymane.iterator(); it.hasNext(); ) {
                    Pieniadz pieniadz = it.next();
                    if (pieniadz.czyMozeBycRozmieniony()) {
                        wynik.add(pieniadz);
                        it.remove();
                        break;
                    }
                }
            }
            kasa.addAll(otrzymane);
        } else {
            int min = 0;

            for (Pieniadz pieniadz : otrzymane) {
                if (!pieniadz.czyMozeBycRozmieniony()) {
                    min = pieniadz.wartosc();
                }
            }
            for (Pieniadz pieniadz : otrzymane) {
                if (!pieniadz.czyMozeBycRozmieniony()) {
                    if (pieniadz.wartosc() < min) {
                        min = pieniadz.wartosc();
                    }
                }
            }
            for (Iterator<Pieniadz> it = otrzymane.iterator(); it.hasNext(); ) {
                Pieniadz pieniadz = it.next();
                if (!pieniadz.czyMozeBycRozmieniony()) {
                    if (pieniadz.wartosc() != min) {
                        kasa.add(pieniadz);
                    } else {
                        wynik.add(pieniadz);
                    }
                    it.remove();
                }
            }

            otrzymane.addAll(kasa);

            while (true) {
                List<Pieniadz> rozmienione = new ArrayList<>();
                boolean check = true;

                for (Iterator<Pieniadz> it = kasa.iterator(); it.hasNext(); ) {
                    Pieniadz pieniadz = it.next();
                    if (pieniadz.czyMozeBycRozmieniony() && pieniadz.wartosc() != 1) {
                        rozmienione = rozmieniacz.rozmien(pieniadz);
                        it.remove();
                        check = false;
                        break;
                    }
                }

                kasa.addAll(rozmienione);

                if (check) {
                    break;
                }
            }

            for (int i = 0; i < reszta; ++i) {
                for (Iterator<Pieniadz> it = kasa.iterator(); it.hasNext(); ) {
                    Pieniadz pieniadz = it.next();
                    if (pieniadz.czyMozeBycRozmieniony()) {
                        wynik.add(pieniadz);
                        it.remove();
                        break;
                    }
                }
            }
            return wynik;
        }
        return wynik;
    }

    @Override
    public List<Pieniadz> stanKasy() {
        return kasa;
    }

    @Override
    public void dostępDoRozmieniacza(RozmieniaczInterface rozmieniacz) {
        this.rozmieniacz = rozmieniacz;
    }

    @Override
    public void dostępDoPoczątkowegoStanuKasy(Supplier<Pieniadz> dostawca) {
        for (Pieniadz pieniadz = dostawca.get(); pieniadz != null; pieniadz = dostawca.get()) {
            kasa.add(pieniadz);
        }
    }
}
