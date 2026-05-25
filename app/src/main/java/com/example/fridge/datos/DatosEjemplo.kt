package com.example.fridge.datos

import com.example.fridge.modelo.Producto

object DatosEjemplo {
    // devuelve una despensa variada para probar la app con alimentos habituales
    fun productos(): List<Producto> {
        return listOf(
            Producto(nombre = "Leche", cantidad = 1, categoria = "Lácteos", diasCaducidad = 2),
            Producto(nombre = "Tomate", cantidad = 4, categoria = "Verdura", diasCaducidad = 3),
            Producto(nombre = "Pollo", cantidad = 1, categoria = "Carne", diasCaducidad = 1),
            Producto(nombre = "Arroz", cantidad = 1, categoria = "Despensa", diasCaducidad = 90),
            Producto(nombre = "Queso", cantidad = 1, categoria = "Lácteos", diasCaducidad = 7),
            Producto(nombre = "Huevos", cantidad = 6, categoria = "Lácteos", diasCaducidad = 12),
            Producto(nombre = "Cebolla", cantidad = 3, categoria = "Verdura", diasCaducidad = 20),
            Producto(nombre = "Ajo", cantidad = 1, categoria = "Verdura", diasCaducidad = 45),
            Producto(nombre = "Patatas", cantidad = 5, categoria = "Verdura", diasCaducidad = 30),
            Producto(nombre = "Zanahorias", cantidad = 4, categoria = "Verdura", diasCaducidad = 18),
            Producto(nombre = "Pimiento", cantidad = 2, categoria = "Verdura", diasCaducidad = 6),
            Producto(nombre = "Calabacín", cantidad = 2, categoria = "Verdura", diasCaducidad = 5),
            Producto(nombre = "Espinacas", cantidad = 1, categoria = "Verdura", diasCaducidad = 3),
            Producto(nombre = "Champiñones", cantidad = 1, categoria = "Verdura", diasCaducidad = 4),
            Producto(nombre = "Lentejas", cantidad = 1, categoria = "Despensa", diasCaducidad = 180),
            Producto(nombre = "Garbanzos", cantidad = 1, categoria = "Despensa", diasCaducidad = 180),
            Producto(nombre = "Pasta", cantidad = 2, categoria = "Despensa", diasCaducidad = 120),
            Producto(nombre = "Pan", cantidad = 1, categoria = "Panadería", diasCaducidad = 2),
            Producto(nombre = "Aceite de oliva", cantidad = 1, categoria = "Condimentos", diasCaducidad = 365),
            Producto(nombre = "Sal", cantidad = 1, categoria = "Condimentos", diasCaducidad = 365),
            Producto(nombre = "Pimienta", cantidad = 1, categoria = "Condimentos", diasCaducidad = 365),
            Producto(nombre = "Pimentón", cantidad = 1, categoria = "Especias", diasCaducidad = 365),
            Producto(nombre = "Orégano", cantidad = 1, categoria = "Especias", diasCaducidad = 365),
            Producto(nombre = "Perejil", cantidad = 1, categoria = "Especias", diasCaducidad = 8),
            Producto(nombre = "Limón", cantidad = 2, categoria = "Fruta", diasCaducidad = 14),
            Producto(nombre = "Manzanas", cantidad = 4, categoria = "Fruta", diasCaducidad = 21),
            Producto(nombre = "Yogur griego", cantidad = 2, categoria = "Lácteos", diasCaducidad = 10),
            Producto(nombre = "Mantequilla", cantidad = 1, categoria = "Lácteos", diasCaducidad = 35),
            Producto(nombre = "Jamón", cantidad = 1, categoria = "Carne", diasCaducidad = 6),
            Producto(nombre = "Atún", cantidad = 3, categoria = "Despensa", diasCaducidad = 240)
        )
    }
}
