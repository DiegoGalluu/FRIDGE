package com.example.fridge.datos

import com.example.fridge.modelo.Producto

object DatosEjemplo {
    // devuelve productos sencillos para probar la app
    fun productos(): List<Producto> {
        return listOf(
            Producto(nombre = "leche", cantidad = 1, categoria = "lacteos", diasCaducidad = 2),
            Producto(nombre = "tomate", cantidad = 4, categoria = "verdura", diasCaducidad = 3),
            Producto(nombre = "pollo", cantidad = 1, categoria = "carne", diasCaducidad = 1),
            Producto(nombre = "arroz", cantidad = 1, categoria = "despensa", diasCaducidad = 90),
            Producto(nombre = "queso", cantidad = 1, categoria = "lacteos", diasCaducidad = 7)
        )
    }
}
