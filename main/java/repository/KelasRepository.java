package repository;

import model.Kelas;
import java.util.*;

public class KelasRepository {

    private List<Kelas> kelasList = new ArrayList<>();

    public void addKelas(Kelas k) {
        kelasList.add(k);
    }

    public List<Kelas> getAll() {
        return kelasList;
    }

    public Kelas findById(String id) {
        for (Kelas k : kelasList) {
            if (k.getIdKelas().equals(id)) {
                return k;
            }
        }
        return null;
    }
}
