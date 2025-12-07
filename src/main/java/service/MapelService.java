package service;

import repository.MapelRepository;

public class MapelService {
    private final MapelRepository mapelRepo;

    public MapelService(MapelRepository mapelRepo) {
        this.mapelRepo = mapelRepo;
    }

    public void deleteMapel(String idMapel) {
        mapelRepo.deleteGuruRelations(idMapel);
        mapelRepo.deleteKelasRelations(idMapel);
        mapelRepo.deleteMapel(idMapel);
    }
}