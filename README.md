# Rail Suite

## Sistema di supervisione e pianificazione ferroviaria

---

### Useful Links

- [Project documentation](https://www.overleaf.com/project/6878e189fb54376e984e33a9)
- [Project DB](https://console.neon.tech/app/projects/blue-credit-23563491?branchId=br-polished-water-a20antqu)
- [JavaFX for gui](https://youtube.com/playlist?list=PLZPZq0r_RZOM-8vJA3NQFZB7JroDcMwev&si=EZBrWkfVWXSPGsL3)

---
## Dsign patterns

- Singleton per postgresConnection.java
- DAO
- Factory/interface-implementation [fixme]
- Composite lo abbiamo nell'interfaccia di Line e di depot
perchè a loro interno utilizziamo delle sottoclassi, lineStation.java e
depotStation.java, che le completano.
- Observer per il sistema di notifica delle linee [fixme]