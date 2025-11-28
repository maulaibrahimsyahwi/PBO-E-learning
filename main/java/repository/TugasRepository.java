package repository;

import model.Tugas;
import java.util.*;

public class TugasRepository {

    private List<Tugas> tugasList = new ArrayList<>();

    public void addTugas(Tugas t) {
        tugasList.add(t);
    }

    public List<Tugas> getAll() {
        return tugasList;
    }

    public Tugas findById(String id) {
        for (Tugas t : tugasList) {
            if (t.getIdTugas().equals(id)) {
                return t;
            }
        }
        return null;
    }
}
