# RailSuiteFacade

Questo file contiene solo le definizioni dei metodi della classe `RailSuiteFacade`.

---

## Definizioni

### Carriage
<span style="color:#0074D9;">Carriage</span> <span style="color:#2ECC40;">selectCarriage</span>(<span style="color:#FFDC00;">int id</span>)<br>
<span style="color:#0074D9;">List&lt;Carriage&gt;</span> <span style="color:#2ECC40;">selectAllCarriages</span>(<span style="color:#FFDC00;">int id</span>)<br>
<span style="color:#0074D9;">List&lt;Carriage&gt;</span> <span style="color:#2ECC40;">selectCarriagesByConvoyId</span>(<span style="color:#FFDC00;">int convoyId</span>)<br>
<span style="color:#0074D9;">boolean</span> <span style="color:#2ECC40;">updateCarriageConvoy</span>(<span style="color:#FFDC00;">int carriageId</span>, <span style="color:#FFDC00;">Integer idConvoy</span>)<br>

### Staff
<span style="color:#0074D9;">Staff</span> <span style="color:#2ECC40;">findStaffById</span>(<span style="color:#FFDC00;">int id</span>)<br>
<span style="color:#0074D9;">Staff</span> <span style="color:#2ECC40;">findStaffByEmail</span>(<span style="color:#FFDC00;">String email</span>)<br>
<span style="color:#0074D9;">List&lt;Staff&gt;</span> <span style="color:#2ECC40;">findAllStaff</span>()<br>
<span style="color:#0074D9;">List&lt;Staff&gt;</span> <span style="color:#2ECC40;">findStaffByType</span>(<span style="color:#FFDC00;">Staff.TypeOfStaff type</span>)<br>

### Station
<span style="color:#0074D9;">Station</span> <span style="color:#2ECC40;">findStationById</span>(<span style="color:#FFDC00;">int id</span>)<br>
<span style="color:#0074D9;">Station</span> <span style="color:#2ECC40;">findStationByLocation</span>(<span style="color:#FFDC00;">String location</span>)<br>
<span style="color:#0074D9;">List&lt;Station&gt;</span> <span style="color:#2ECC40;">findAllStations</span>()<br>
<span style="color:#0074D9;">List&lt;Station&gt;</span> <span style="color:#2ECC40;">findAllHeadStations</span>()<br>

### Line
<span style="color:#0074D9;">Line</span> <span style="color:#2ECC40;">findLineById</span>(<span style="color:#FFDC00;">int idLine</span>)<br>
<span style="color:#0074D9;">List&lt;Line&gt;</span> <span style="color:#2ECC40;">findAllLines</span>()<br>
<span style="color:#0074D9;">List&lt;Line&gt;</span> <span style="color:#2ECC40;">findLinesByStation</span>(<span style="color:#FFDC00;">int idStation</span>)<br>

### Convoy
<span style="color:#0074D9;">Convoy</span> <span style="color:#2ECC40;">selectConvoy</span>(<span style="color:#FFDC00;">int id</span>)<br>
<span style="color:#0074D9;">List&lt;Convoy&gt;</span> <span style="color:#2ECC40;">selectAllConvoys</span>()<br>
<span style="color:#0074D9;">boolean</span> <span style="color:#2ECC40;">removeConvoy</span>(<span style="color:#FFDC00;">int id</span>)<br>
<span style="color:#0074D9;">boolean</span> <span style="color:#2ECC40;">addCarriageToConvoy</span>(<span style="color:#FFDC00;">int convoyId</span>, <span style="color:#FFDC00;">Carriage carriage</span>)<br>
<span style="color:#0074D9;">boolean</span> <span style="color:#2ECC40;">removeCarriageFromConvoy</span>(<span style="color:#FFDC00;">int convoyId</span>, <span style="color:#FFDC00;">Carriage carriage</span>)<br>
<span style="color:#0074D9;">Integer</span> <span style="color:#2ECC40;">findConvoyIdByCarriageId</span>(<span style="color:#FFDC00;">int carriageId</span>)<br>
<span style="color:#0074D9;">Convoy</span> <span style="color:#2ECC40;">createConvoy</span>(<span style="color:#FFDC00;">List&lt;Carriage&gt; carriages</span>)<br>


### ConvoyPool
<span style="color:#0074D9;">ConvoyPool</span> <span style="color:#2ECC40;">getConvoyPoolById</span>(<span style="color:#FFDC00;">int idConvoy</span>)<br>
<span style="color:#0074D9;">void</span> <span style="color:#2ECC40;">updateConvoyPool</span>(<span style="color:#FFDC00;">ConvoyPool convoyPool</span>)<br>
<span style="color:#0074D9;">List&lt;ConvoyPool&gt;</span> <span style="color:#2ECC40;">getAllConvoyPools</span>()<br>
<span style="color:#0074D9;">List&lt;ConvoyPool&gt;</span> <span style="color:#2ECC40;">getConvoysByStation</span>(<span style="color:#FFDC00;">int idStation</span>)<br>
<span style="color:#0074D9;">List&lt;ConvoyPool&gt;</span> <span style="color:#2ECC40;">getConvoysByStatus</span>(<span style="color:#FFDC00;">ConvoyPool.ConvoyStatus status</span>)<br>
<span style="color:#0074D9;">List&lt;ConvoyPool&gt;</span> <span style="color:#2ECC40;">getConvoysByStationAndStatus</span>(<span style="color:#FFDC00;">int idStation</span>, <span style="color:#FFDC00;">ConvoyPool.ConvoyStatus status</span>)<br>


### StaffPool
<span style="color:#0074D9;">StaffPool</span> <span style="color:#2ECC40;">findStaffPoolById</span>(<span style="color:#FFDC00;">int idStaff</span>)<br>
<span style="color:#0074D9;">List&lt;StaffPool&gt;</span> <span style="color:#2ECC40;">findStaffPoolByStation</span>(<span style="color:#FFDC00;">int idStation</span>)<br>
<span style="color:#0074D9;">void</span> <span style="color:#2ECC40;">updateStaffPool</span>(<span style="color:#FFDC00;">StaffPool staffPool</span>)<br>
<span style="color:#0074D9;">List&lt;StaffPool&gt;</span> <span style="color:#2ECC40;">findStaffPoolByStatus</span>(<span style="color:#FFDC00;">StaffPoolDao.ShiftStatus status</span>)<br>
<span style="color:#0074D9;">List&lt;StaffPool&gt;</span> <span style="color:#2ECC40;">findStaffPoolByStatusAndStation</span>(<span style="color:#FFDC00;">StaffPoolDao.ShiftStatus status</span>, <span style="color:#FFDC00;">int idStation</span>)<br>


### LineStation
<span style="color:#0074D9;">LineStation</span> <span style="color:#2ECC40;">findLineStationById</span>(<span style="color:#FFDC00;">int idLine</span>, <span style="color:#FFDC00;">int idStation</span>)<br>
<span style="color:#0074D9;">List&lt;LineStation&gt;</span> <span style="color:#2ECC40;">findLineStationsByLine</span>(<span style="color:#FFDC00;">int idLine</span>)<br>


### CarriageDepot
<span style="color:#0074D9;">CarriageDepot</span> <span style="color:#2ECC40;">getCarriageDepot</span>(<span style="color:#FFDC00;">int idDepot</span>, <span style="color:#FFDC00;">int idCarriage</span>)<br>
<span style="color:#0074D9;">List&lt;CarriageDepot&gt;</span> <span style="color:#2ECC40;">getCarriagesByDepot</span>(<span style="color:#FFDC00;">int idDepot</span>)<br>


### Depot
<span style="color:#0074D9;">Depot</span> <span style="color:#2ECC40;">getDepot</span>(<span style="color:#FFDC00;">int idDepot</span>)<br>
<span style="color:#0074D9;">List&lt;Depot&gt;</span> <span style="color:#2ECC40;">getAllDepots</span>()<br>
<span style="color:#0074D9;">void</span> <span style="color:#2ECC40;">insertDepot</span>(<span style="color:#FFDC00;">int idDepot</span>)<br>
<span style="color:#0074D9;">void</span> <span style="color:#2ECC40;">deleteDepot</span>(<span style="color:#FFDC00;">int idDepot</span>)<br>


### Run
<span style="color:#0074D9;">Run</span> <span style="color:#2ECC40;">selectRunByLineAndConvoy</span>(<span style="color:#FFDC00;">int idLine</span>, <span style="color:#FFDC00;">int idConvoy</span>)<br>
<span style="color:#0074D9;">Run</span> <span style="color:#2ECC40;">selectRun</span>(<span style="color:#FFDC00;">int idLine</span>, <span style="color:#FFDC00;">int idConvoy</span>, <span style="color:#FFDC00;">int idStaff</span>)<br>
<span style="color:#0074D9;">Run</span> <span style="color:#2ECC40;">selectRunByStaffAndConvoy</span>(<span style="color:#FFDC00;">int idStaff</span>, <span style="color:#FFDC00;">int idConvoy</span>)<br>
<span style="color:#0074D9;">Run</span> <span style="color:#2ECC40;">selectRunByStaffAndLine</span>(<span style="color:#FFDC00;">int idStaff</span>, <span style="color:#FFDC00;">int idLine</span>)<br>
<span style="color:#0074D9;">List&lt;Run&gt;</span> <span style="color:#2ECC40;">selectAllRuns</span>()<br>
<span style="color:#0074D9;">boolean</span> <span style="color:#2ECC40;">removeRun</span>(<span style="color:#FFDC00;">int idLine</span>, <span style="color:#FFDC00;">int idConvoy</span>)<br>
<span style="color:#0074D9;">Run</span> <span style="color:#2ECC40;">createRun</span>(<span style="color:#FFDC00;">int idLine</span>, <span style="color:#FFDC00;">int idConvoy</span>, <span style="color:#FFDC00;">int idStaff</span>, <span style="color:#FFDC00;">java.sql.Time timeDeparture</span>, <span style="color:#FFDC00;">java.sql.Time timeArrival</span>, <span style="color:#FFDC00;">int idFirstStation</span>, <span style="color:#FFDC00;">int idLastStation</span>)<br>
<span style="color:#0074D9;">boolean</span> <span style="color:#2ECC40;">updateRun</span>(<span style="color:#FFDC00;">int idLine</span>, <span style="color:#FFDC00;">int idConvoy</span>, <span style="color:#FFDC00;">int idStaff</span>, <span style="color:#FFDC00;">java.sql.Time timeDeparture</span>, <span style="color:#FFDC00;">java.sql.Time timeArrival</span>, <span style="color:#FFDC00;">int idFirstStation</span>, <span style="color:#FFDC00;">int idLastStation</span>)<br>
<span style="color:#0074D9;">List&lt;Run&gt;</span> <span style="color:#2ECC40;">selectRunsByStaff</span>(<span style="color:#FFDC00;">int idStaff</span>)<br>
<span style="color:#0074D9;">List&lt;Run&gt;</span> <span style="color:#2ECC40;">selectRunsByLine</span>(<span style="color:#FFDC00;">int idLine</span>)<br>
<span style="color:#0074D9;">List&lt;Run&gt;</span> <span style="color:#2ECC40;">selectRunsByConvoy</span>(<span style="color:#FFDC00;">int idConvoy</span>)<br>
<span style="color:#0074D9;">List&lt;Run&gt;</span> <span style="color:#2ECC40;">selectRunsByFirstStation</span>(<span style="color:#FFDC00;">int idFirstStation</span>)<br>
<span style="color:#0074D9;">List&lt;Run&gt;</span> <span style="color:#2ECC40;">selectRunsByLastStation</span>(<span style="color:#FFDC00;">int idLastStation</span>)<br>
<span style="color:#0074D9;">List&lt;Run&gt;</span> <span style="color:#2ECC40;">selectRunsByFirstStationAndDeparture</span>(<span style="color:#FFDC00;">int idFirstStation</span>, <span style="color:#FFDC00;">java.sql.Time timeDeparture</span>)
