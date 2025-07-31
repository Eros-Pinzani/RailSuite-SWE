package businessLogic.service;

import dao.RunDao;
import domain.Run;
import domain.Line;
import domain.Convoy;
import domain.Staff;

import java.util.*;

public class ManageRunService {
    private final RunDao runDao = RunDao.of();
    private final LineService lineService = new LineService();
    private final ConvoyService convoyService = new ConvoyService();
    private final StaffService staffService = new StaffService();

    // Mappe di associazione
    private class Association {
        private final Integer idLine;
        private final Integer idConvoy;
        private final Integer idStaff;

        public Association(Integer idLine, Integer idConvoy, Integer idStaff) {
            this.idLine = idLine;
            this.idConvoy = idConvoy;
            this.idStaff = idStaff;
        }

        public Integer getIdLine() {
            return idLine;
        }

        public Integer getIdConvoy() {
            return idConvoy;
        }

        public Integer getIdStaff() {
            return idStaff;
        }
    }

    private class AssociationsList {
        public AssociationsList() {}

        private final List<Association> associations = new ArrayList<>();

        public void addAssociation(Integer idLine, Integer idConvoy, Integer idStaff) {
            associations.add(new Association(idLine, idConvoy, idStaff));
        }

        public List<Association> getByLine(Integer idLine) {
            if (!associations.isEmpty()) {
                List<Association> result = new ArrayList<>();
                for (Association association : associations) {
                    if (association.getIdLine().equals(idLine)) {
                        result.add(association);
                    }
                }
                return result;
            } else {
                return Collections.emptyList();
            }
        }

        public List<Association> getByConvoy(Integer idConvoy) {
            if (!associations.isEmpty()) {
                List<Association> result = new ArrayList<>();
                for (Association association : associations) {
                    if (association.getIdConvoy().equals(idConvoy)) {
                        result.add(association);
                    }
                }
                return result;
            } else {
                return Collections.emptyList();
            }
        }

        public List<Association> getByStaff(Integer idStaff) {
            if (!associations.isEmpty()) {
                List<Association> result = new ArrayList<>();
                for (Association association : associations) {
                    if (association.getIdStaff().equals(idStaff)) {
                        result.add(association);
                    }
                }
                return result;
            } else {
                return Collections.emptyList();
            }
        }

        public List<Association> getAll() {
            return new ArrayList<>(associations);
        }

        public List<Association> getByLineAndConvoy(Integer idLine, Integer idConvoy) {
            if (!associations.isEmpty()) {
                List<Association> result = new ArrayList<>();
                for (Association association : associations) {
                    if (association.getIdLine().equals(idLine) && association.getIdConvoy().equals(idConvoy)) {
                        result.add(association);
                    }
                }
                return result;
            } else {
                return Collections.emptyList();
            }
        }

        public List<Association> getByLineAndStaff(Integer idLine, Integer idStaff) {
            if (!associations.isEmpty()) {
                List<Association> result = new ArrayList<>();
                for (Association association : associations) {
                    if (association.getIdLine().equals(idLine) && association.getIdStaff().equals(idStaff)) {
                        result.add(association);
                    }
                }
                return result;
            } else {
                return Collections.emptyList();
            }
        }

        public List<Association> getByConvoyAndStaff(Integer idConvoy, Integer idStaff) {
            if (!associations.isEmpty()) {
                List<Association> result = new ArrayList<>();
                for (Association association : associations) {
                    if (association.getIdConvoy().equals(idConvoy) && association.getIdStaff().equals(idStaff)) {
                        result.add(association);
                    }
                }
                return result;
            } else {
                return Collections.emptyList();
            }
        }

        public List<Association> getByLineConvoyAndStaff(Integer idLine, Integer idConvoy, Integer idStaff) {
            if (!associations.isEmpty()) {
                List<Association> result = new ArrayList<>();
                for (Association association : associations) {
                    if (association.getIdLine().equals(idLine) && association.getIdConvoy().equals(idConvoy) && association.getIdStaff().equals(idStaff)) {
                        result.add(association);
                    }
                }
                return result;
            } else {
                return Collections.emptyList();
            }
        }
    }

    // Mappe di supporto per lookup rapido (solo per JavaFX)
    private Map<Integer, Set<Integer>> lineToConvoys = new HashMap<>();
    private Map<Integer, Set<Integer>> lineToOperators = new HashMap<>();
    private Map<Integer, Set<Integer>> convoyToLines = new HashMap<>();
    private Map<Integer, Set<Integer>> convoyToOperators = new HashMap<>();
    private Map<Integer, Set<Integer>> operatorToLines = new HashMap<>();
    private Map<Integer, Set<Integer>> operatorToConvoys = new HashMap<>();

    private boolean initialized = false;
    private final AssociationsList associationsList = new AssociationsList();
    private void initializeAssociations() {
        if (initialized) return;
        List<Run> allRuns = getFilteredRuns(null, null, null);
        for (Run run : allRuns) {
            int idLine = run.getIdLine();
            int idConvoy = run.getIdConvoy();
            int idStaff = run.getIdStaff();
            associationsList.addAssociation(idLine, idConvoy, idStaff);
            // Popola le mappe di supporto
            lineToConvoys.computeIfAbsent(idLine, k -> new HashSet<>()).add(idConvoy);
            lineToOperators.computeIfAbsent(idLine, k -> new HashSet<>()).add(idStaff);
            convoyToLines.computeIfAbsent(idConvoy, k -> new HashSet<>()).add(idLine);
            convoyToOperators.computeIfAbsent(idConvoy, k -> new HashSet<>()).add(idStaff);
            operatorToLines.computeIfAbsent(idStaff, k -> new HashSet<>()).add(idLine);
            operatorToConvoys.computeIfAbsent(idStaff, k -> new HashSet<>()).add(idConvoy);
        }
        initialized = true;
    }

    public List<Line> getAllLines() {
        return lineService.getAllLines();
    }

    public List<Convoy> getAllConvoys() {
        return convoyService.getAllConvoys();
    }

    public List<Staff> getAllOperators() {
        return staffService.getAllOperators();
    }

    // --- Nuovi metodi che restituiscono List<Association> invece di Set<Integer> ---
    public List<Association> getAssociationsByLine(Integer idLine) {
        initializeAssociations();
        return associationsList.getByLine(idLine);
    }

    public List<Association> getAssociationsByConvoy(Integer idConvoy) {
        initializeAssociations();
        return associationsList.getByConvoy(idConvoy);
    }

    public List<Association> getAssociationsByStaff(Integer idStaff) {
        initializeAssociations();
        return associationsList.getByStaff(idStaff);
    }

    public List<Association> getAssociationsByLineAndConvoy(Integer idLine, Integer idConvoy) {
        initializeAssociations();
        return associationsList.getByLineAndConvoy(idLine, idConvoy);
    }

    public List<Association> getAssociationsByLineAndStaff(Integer idLine, Integer idStaff) {
        initializeAssociations();
        return associationsList.getByLineAndStaff(idLine, idStaff);
    }

    public List<Association> getAssociationsByConvoyAndStaff(Integer idConvoy, Integer idStaff) {
        initializeAssociations();
        return associationsList.getByConvoyAndStaff(idConvoy, idStaff);
    }

    public List<Association> getAssociationsByLineConvoyAndStaff(Integer idLine, Integer idConvoy, Integer idStaff) {
        initializeAssociations();
        return associationsList.getByLineConvoyAndStaff(idLine, idConvoy, idStaff);
    }

    public List<Association> getAllAssociations() {
        initializeAssociations();
        return associationsList.getAll();
    }

    /**
     * Returns the runs filtered according to the provided parameters.
     * If a parameter is null, it is not used as a filter.
     * If all are null, returns an empty list.
     */
    public List<Run> getFilteredRuns(Integer idLine, Integer idConvoy, Integer idStaff) {
        try {
            return runDao.selectRunsFiltered(idLine, idConvoy, idStaff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
