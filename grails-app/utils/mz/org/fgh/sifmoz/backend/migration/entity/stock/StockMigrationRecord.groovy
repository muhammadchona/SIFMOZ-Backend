package mz.org.fgh.sifmoz.backend.migration.entity.stock

import mz.org.fgh.sifmoz.backend.drug.Drug
import mz.org.fgh.sifmoz.backend.migration.base.record.AbstractMigrationRecord
import mz.org.fgh.sifmoz.backend.migration.base.record.MigratedRecord
import mz.org.fgh.sifmoz.backend.migration.common.MigrationError
import mz.org.fgh.sifmoz.backend.migrationLog.MigrationLog
import mz.org.fgh.sifmoz.backend.stock.Stock
import mz.org.fgh.sifmoz.backend.clinic.Clinic
import mz.org.fgh.sifmoz.backend.stockcenter.StockCenter
import mz.org.fgh.sifmoz.backend.stockentrance.StockEntrance

import java.text.DateFormat
import java.text.SimpleDateFormat

class StockMigrationRecord extends AbstractMigrationRecord {

    private Integer id

    private String batchnumber

    private Date datereceived

    private Date expirydate

    private char modified

    private String shelfnumber

    private String numeroguia

    private int unitsreceived

    private String manufacturer

    private char hasunitsremaining

    private BigDecimal unitprice

    private String stockcentername

    String atccode_id

    private int fullcontainersremaining

    String name

    private boolean  mainclinic

    private String clinicname

    private StockTakeMigrationRecord stockTakeMigrationRecord

    private StockAdjustmentMigrationRecord stockAdjustmentMigrationRecord

    //----------------------------------------------------

    @Override
    List<MigrationLog> migrate() {
        List<MigrationLog> logs = new ArrayList<>()
        Stock.withTransaction {
            //Declarations
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy")
            String dateFormated = dateFormat.format(this.datereceived)
            Date receivedDate = dateFormat.parse(dateFormated)
            def stockEntranceAux = null

            def clinic = Clinic.findWhere(mainClinic: true)
            def stockEntranceIfAlreadyExist = StockEntrance.findWhere(dateReceived: receivedDate)
            def stockCenterAux = StockCenter.findWhere(name: this.stockcentername)

            def drugAux = Drug.findByFnmCode(this.atccode_id)
            if(drugAux == null){
                drugAux = Drug.findByName(this.name)
            }

            // Setting StockEntrance
            StockEntrance stockEntrance = new  StockEntrance()
            stockEntrance.setClinic(clinic)
            stockEntrance.setDateReceived(receivedDate)
            stockEntrance.setOrderNumber(dateFormated + "_NA")

            // Setting Stock
            getMigratedRecord().setBatchNumber(this.batchnumber.length() > 0 ? this.batchnumber : dateFormated +"_"+ drugAux.fnmCode)
            getMigratedRecord().setModified(this.modified == (char) 'T')
            getMigratedRecord().setUnitsReceived(this.unitsreceived)
            getMigratedRecord().setExpireDate(this.expirydate)
            getMigratedRecord().setShelfNumber(this.shelfnumber)
            getMigratedRecord().setManufacture(this.manufacturer)
            getMigratedRecord().setHasUnitsRemaining(this.hasunitsremaining == (char) 'T')
            if(this.fullcontainersremaining != null){
                getMigratedRecord().setStockMoviment(this.fullcontainersremaining)
            } else {
                getMigratedRecord().setStockMoviment(0)
            }


            //LOGS
            if (clinic != null) {
                getMigratedRecord().setClinic(clinic)
            } else {
                String[] msg = {String.format(MigrationError.MAIN_CLINIC_NOT_IDENTIFIED.getDescription())}
                logs.add(new MigrationLog(MigrationError.MAIN_CLINIC_NOT_IDENTIFIED.getCode(),msg,getId(), getEntityName()))
            }

            if (stockCenterAux != null) {
                getMigratedRecord().setCenter(stockCenterAux)
            } else {
                String[] msg = {String.format(MigrationError.STOCK_CENTER_NOT_FOUND.getDescription(), this.stockcentername)}
                logs.add(new MigrationLog(MigrationError.STOCK_CENTER_NOT_FOUND.getCode(),msg,getId(), getEntityName()))
            }

            if (drugAux != null) {
                getMigratedRecord().setDrug(drugAux)
            } else {
                String[] msg = {String.format(MigrationError.DRUG_NOT_FOUND.getDescription(), this.atccode_id != null ? this.atccode_id : this.name)}
                logs.add(new MigrationLog(MigrationError.DRUG_NOT_FOUND.getCode(),msg,getId(), getEntityName()))
            }

            // The business Logic
            if (stockEntranceIfAlreadyExist != null) {// Ja existe o stockEntrance // migrar apenas o Stock
                getMigratedRecord().setEntrance(stockEntranceIfAlreadyExist) // Setting numero de guia => (id do stockEntrance)
                if (getMigratedRecord().hasErrors()) {
                    MigrationLog migrationLog = new MigrationLog()
                    migrationLog.errors = getMigratedRecord().errors.toString()
                    logs.add(migrationLog)
                    System.out.println(migrationLog)
                }

                if (getMigratedRecord().hasErrors()) {
//                    MigrationLog migrationLog = new MigrationLog()
//                    migrationLog.errors = getMigratedRecord().errors.toString()
//                    logs.add(migrationLog)
                    logs.addAll(generateUnknowMigrationLog(this, getMigratedRecord().getErrors().toString()))
                }

                if(logs.size() == 0){
                    def createdStock =  getMigratedRecord().save(flush: true)
                    if(createdStock == null) {
                        logs.add(new MigrationLog("800","Nao Criado",getId(), getEntityName()))
                    }
                }

            } else {// O stockEntrance ainda nao existe

                if (stockEntrance.hasErrors()) {
//                    MigrationLog migrationLog = new MigrationLog()
//                    migrationLog.errors = stockEntrance().errors
//                    logs.add(migrationLog)
//                    System.out.println(migrationLog)
                    logs.addAll(generateUnknowMigrationLog(this, stockEntrance.getErrors().toString()))
                }else{
                    stockEntranceAux = stockEntrance.save(flush: true)
                }

                if (stockEntranceAux != null) {
                    getMigratedRecord().setEntrance(stockEntranceAux)
                }else{
                    String[] msg = {String.format(MigrationError.STOCK_ENTRANCE_NOT_FOUND.getDescription())}
                    logs.add(new MigrationLog(MigrationError.STOCK_ENTRANCE_NOT_FOUND.getCode(),msg,getId(), getEntityName()))
                }

                if (getMigratedRecord().hasErrors()) {
//                    MigrationLog migrationLog = new MigrationLog()
//                    migrationLog.errors = getMigratedRecord().errors.toString()
//                    logs.add(migrationLog)
                    logs.addAll(generateUnknowMigrationLog(this, getMigratedRecord().getErrors().toString()))
                }

                if(logs.size() == 0){
                      def createdStock =  getMigratedRecord().save(flush: true)
                    if(createdStock == null) {
                        logs.add(new MigrationLog("800","Nao Criado",getId(), getEntityName()))
                    }
                }
            }
        }

        return logs
    }

    @Override
    void updateIDMEDInfo() {

    }

    @Override
    int getId() {
        return this.id
    }

    @Override
    String getEntityName() {
        return "stock"
    }

    @Override
    MigratedRecord initMigratedRecord() {
        return new Stock()
    }

    Stock getMigratedRecord() {
        return (Stock) super.getMigratedRecord()
    }
}
