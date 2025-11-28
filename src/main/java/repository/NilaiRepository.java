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
        List<Nilai> result = new ArrayList<>();
        for (Nilai n : nilaiList) {
            if (n.getIdUser().equals(idUser)) {
                result.add(n);
            }
        }
        return result;
    }
}
