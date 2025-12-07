package context;

import repository.*;
import service.*;

public class AppContext {
    private static AppContext instance;

    private final UserRepository userRepo;
    private final KelasRepository kelasRepo;
    private final MapelRepository mapelRepo;
    private final MateriRepository materiRepo;
    private final TugasRepository tugasRepo;
    private final UjianRepository ujianRepo;
    private final JawabanRepository jawabanRepo;
    private final NilaiRepository nilaiRepo;
    private final ForumRepository forumRepo;
    private final AbsensiRepository absensiRepo;
    private final SoalRepository soalRepo;

    private final AuthService authService;
    private final MapelService mapelService;

    private AppContext() {
        this.userRepo = new UserRepository();
        this.kelasRepo = new KelasRepository();
        this.mapelRepo = new MapelRepository();
        this.materiRepo = new MateriRepository();
        this.tugasRepo = new TugasRepository();
        this.ujianRepo = new UjianRepository();
        this.jawabanRepo = new JawabanRepository();
        this.nilaiRepo = new NilaiRepository();
        this.forumRepo = new ForumRepository();
        this.absensiRepo = new AbsensiRepository();
        this.soalRepo = new SoalRepository();

        this.authService = new AuthService(userRepo);
        this.mapelService = new MapelService(mapelRepo);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public UserRepository getUserRepo() { return userRepo; }
    public KelasRepository getKelasRepo() { return kelasRepo; }
    public MapelRepository getMapelRepo() { return mapelRepo; }
    public MateriRepository getMateriRepo() { return materiRepo; }
    public TugasRepository getTugasRepo() { return tugasRepo; }
    public UjianRepository getUjianRepo() { return ujianRepo; }
    public JawabanRepository getJawabanRepo() { return jawabanRepo; }
    public NilaiRepository getNilaiRepo() { return nilaiRepo; }
    public ForumRepository getForumRepo() { return forumRepo; }
    public AbsensiRepository getAbsensiRepo() { return absensiRepo; }
    public SoalRepository getSoalRepo() { return soalRepo; }

    public AuthService getAuthService() { return authService; }
    public MapelService getMapelService() { return mapelService; }
}