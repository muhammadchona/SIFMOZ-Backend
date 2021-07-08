package mz.org.fgh.sifmoz.backend.group

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class GroupController {

    GroupService groupService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond groupService.list(params), model:[groupCount: groupService.count()]
    }

    def show(Long id) {
        respond groupService.get(id)
    }

    @Transactional
    def save(Group group) {
        if (group == null) {
            render status: NOT_FOUND
            return
        }
        if (group.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond group.errors
            return
        }

        try {
            groupService.save(group)
        } catch (ValidationException e) {
            respond group.errors
            return
        }

        respond group, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Group group) {
        if (group == null) {
            render status: NOT_FOUND
            return
        }
        if (group.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond group.errors
            return
        }

        try {
            groupService.save(group)
        } catch (ValidationException e) {
            respond group.errors
            return
        }

        respond group, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || groupService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }
}