package mz.org.fgh.sifmoz.backend.patientVisit

import mz.org.fgh.sifmoz.backend.clinic.Clinic
import mz.org.fgh.sifmoz.backend.patient.Patient
import mz.org.fgh.sifmoz.backend.patientVisitDetails.PatientVisitDetails
import mz.org.fgh.sifmoz.backend.screening.AdherenceScreening
import mz.org.fgh.sifmoz.backend.screening.PregnancyScreening
import mz.org.fgh.sifmoz.backend.screening.RAMScreening
import mz.org.fgh.sifmoz.backend.screening.TBScreening
import mz.org.fgh.sifmoz.backend.screening.VitalSignsScreening

class PatientVisit {
    String id
    Date visitDate
    Clinic clinic

    static belongsTo = [patient: Patient]

    static hasMany = [patientVisitDetails: PatientVisitDetails, adherenceScreening: AdherenceScreening, vitalSigns: VitalSignsScreening,
                      pregnancyScreening : PregnancyScreening, tbScreening: TBScreening, ramScreening: RAMScreening]


    static mapping = {
        id generator: "uuid"
    }

    static constraints = {
    }
}
