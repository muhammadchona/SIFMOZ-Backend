package mz.org.fgh.sifmoz.backend.screening

import grails.rest.Resource
import mz.org.fgh.sifmoz.backend.patientVisit.PatientVisit

@Resource(uri='/api/vitalSignsScreening')
class VitalSignsScreening {

    int distort;
    String imc;
    double weight;
    int systole;
    double height;
    PatientVisit visit

    static mapping = {
        version false
    }

    static constraints = {
        distort (nullable: false)
    }

    @Override
    public String toString() {
        return "VitalSignsScreening{" +
                "distort=" + distort +
                ", imc='" + imc + '\'' +
                ", weight=" + weight +
                ", systole=" + systole +
                ", height=" + height +
                '}';
    }
}