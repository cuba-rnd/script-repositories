package com.company.rnd.scriptrepo.core

def renameCustomer (String customerId, newName) {
    return "1 - Customer with ${customerId} was renamed to ${newName}".toString()
}

def renameCustomer (UUID customerId, newName) {
    return "2 - Customer with ${customerId} was renamed to ${newName}".toString()
}