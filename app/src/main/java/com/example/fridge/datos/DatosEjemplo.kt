package com.example.fridge.datos

import com.example.fridge.modelo.Producto

object DatosEjemplo {
    // devuelve productos sencillos para probar la app
    fun productos(): List<Producto> {
        return listOf(
            Producto(nombre = "Leche", cantidad = 1, categoria = "Lácteos", diasCaducidad = 2),
            Producto(nombre = "Tomate", cantidad = 4, categoria = "Verdura", diasCaducidad = 3),
            Producto(nombre = "Pollo", cantidad = 1, categoria = "Carne", diasCaducidad = 1),
            Producto(nombre = "Arroz", cantidad = 1, categoria = "Despensa", diasCaducidad = 90),
            Producto(nombre = "Queso", cantidad = 1, categoria = "Lácteos", diasCaducidad = 7)
        )
    }
}
