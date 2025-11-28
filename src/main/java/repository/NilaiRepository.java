package repository;

import model.Nilai;
import java.util.*;

public class NilaiRepository {

    private List<Nilai> nilaiList = new ArrayList<>();

    public void addNilai(Nilai n) {
        nilaiList.add(n);
    }

    public List<Nilai> getAll() {
        return nilaiList;
    }

    public List<Nilai> findBySiswa(String idUser) {
        List<Nilai> hasil = new ArrayList<>();
        for (Nilai n : nilaiList) {
            if (n.getSiswa() != null &&
                n.getSiswa().getIdUser().equals(idUser)) {
                hasil.add(n);
            }
        }
        return hasil;
    }
}
