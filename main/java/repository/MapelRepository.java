package repository;

import model.MataPelajaran;
import java.util.*;

public class MapelRepository {

    private List<MataPelajaran> mapelList = new ArrayList<>();

    public void addMapel(MataPelajaran m) {
        mapelList.add(m);
    }

    public List<MataPelajaran> getAll() {
        return mapelList;
    }

    public MataPelajaran findById(String id) {
        for (MataPelajaran m : mapelList) {
            if (m.getIdMapel().equals(id)) {
                return m;
            }
        }
        return null;
    }
}
