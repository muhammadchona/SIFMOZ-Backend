package mz.org.fgh.sifmoz.backend.healthInformationSystem

import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

import grails.gorm.transactions.Transactional

class HealthInformationSystemController extends RestfulController{

    HealthInformationSystemService healthInformationSystemService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    HealthInformationSystemController() {
        super(HealthInformationSystem)
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        JSON.use('deep'){
            render healthInformationSystemService.list(params) as JSON
        }
    }

    def show(Long id) {
        JSON.use('deep'){
            render healthInformationSystemService.get(id) as JSON
        }
    }

    @Transactional
    def save(HealthInformationSystem healthInformationSystem) {
        if (healthInformationSystem == null) {
            render status: NOT_FOUND
            return
        }
        if (healthInformationSystem.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond healthInformationSystem.errors
            return
        }

        try {
            healthInformationSystemService.save(healthInformationSystem)
        } catch (ValidationException e) {
            respond healthInformationSystem.errors
            return
        }

        respond healthInformationSystem, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(HealthInformationSystem healthInformationSystem) {
        if (healthInformationSystem == null) {
            render status: NOT_FOUND
            return
        }
        if (healthInformationSystem.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond healthInformationSystem.errors
            return
        }

        try {
            healthInformationSystemService.save(healthInformationSystem)
        } catch (ValidationException e) {
            respond healthInformationSystem.errors
            return
        }

        respond healthInformationSystem, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || healthInformationSystemService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }
}