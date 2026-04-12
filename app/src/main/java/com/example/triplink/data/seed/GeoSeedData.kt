package com.example.triplink.data.seed

import com.example.triplink.data.model.Ciudad
import com.example.triplink.data.model.Departamento

object GeoSeedData {
    val departamentos: List<Departamento> = listOf(
        Departamento("Amazonas", listOf(Ciudad("Leticia"), Ciudad("Puerto Nariño"), Ciudad("La Chorrera"))),
        Departamento("Antioquia", listOf(Ciudad("Medellín"), Ciudad("Envigado"), Ciudad("Itaguí"), Ciudad("Rionegro"), Ciudad("Bello"), Ciudad("Sabaneta"), Ciudad("La Estrella"), Ciudad("Copacabana"))),
        Departamento("Arauca", listOf(Ciudad("Arauca"), Ciudad("Saravena"), Ciudad("Fortul"), Ciudad("Cravo Norte"))),
        Departamento("Atlántico", listOf(Ciudad("Barranquilla"), Ciudad("Soledad"), Ciudad("Malambo"), Ciudad("Galapa"), Ciudad("Puerto Colombia"))),
        Departamento("Bolívar", listOf(Ciudad("Cartagena"), Ciudad("Turbaco"), Ciudad("Magangué"), Ciudad("Santa Marta"), Ciudad("El Carmen de Bolívar"))),
        Departamento("Boyacá", listOf(Ciudad("Tunja"), Ciudad("Duitama"), Ciudad("Sogamoso"), Ciudad("Ipiales"), Ciudad("Zipaquirá"), Ciudad("Paipa"))),
        Departamento("Caldas", listOf(Ciudad("Manizales"), Ciudad("Villamaría"), Ciudad("Chinchiná"), Ciudad("Neira"), Ciudad("Palestina"))),
        Departamento("Caquetá", listOf(Ciudad("Florencia"), Ciudad("San Vicente del Caguán"), Ciudad("Montañita"), Ciudad("Cartagena del Chairá"))),
        Departamento("Casanare", listOf(Ciudad("Yopal"), Ciudad("Aguazul"), Ciudad("Monterrey"), Ciudad("Paz de Ariporo"))),
        Departamento("Cauca", listOf(Ciudad("Popayán"), Ciudad("Santander de Quilichao"), Ciudad("Puerto Tejada"), Ciudad("Piendamó"), Ciudad("Silvia"))),
        Departamento("Cesar", listOf(Ciudad("Valledupar"), Ciudad("Bosconia"), Ciudad("Agustín Codazzi"), Ciudad("Chiriguaná"), Ciudad("El Copey"))),
        Departamento("Chocó", listOf(Ciudad("Quibdó"), Ciudad("Istmina"), Ciudad("Unguía"), Ciudad("Riosucio"), Ciudad("Condoto"))),
        Departamento("Córdoba", listOf(Ciudad("Montería"), Ciudad("Cereté"), Ciudad("Lorica"), Ciudad("Puerto Escondido"), Ciudad("Chinú"))),
        Departamento("Cundinamarca", listOf(Ciudad("Bogotá"), Ciudad("Soacha"), Ciudad("Zipaquirá"), Ciudad("Girardot"), Ciudad("Fusagasugá"), Ciudad("Facatativá"), Ciudad("La Mesa"))),
        Departamento("Guainía", listOf(Ciudad("Inírida"), Ciudad("Barrancominas"), Ciudad("San Felipe"))),
        Departamento("Guaviare", listOf(Ciudad("San José del Guaviare"), Ciudad("Miraflores"), Ciudad("Calamar"))),
        Departamento("Huila", listOf(Ciudad("Neiva"), Ciudad("Pitalito"), Ciudad("Garzón"), Ciudad("Isnos"), Ciudad("La Plata"))),
        Departamento("Magdalena", listOf(Ciudad("Santa Marta"), Ciudad("Gaira"), Ciudad("Ciénaga"), Ciudad("Fundación"), Ciudad("Aracataca"))),
        Departamento("Meta", listOf(Ciudad("Villavicencio"), Ciudad("Acacías"), Ciudad("Granada"), Ciudad("Restrepo"), Ciudad("San Juan de Arama"))),
        Departamento("Nariño", listOf(Ciudad("San Juan de Pasto"), Ciudad("Ipiales"), Ciudad("Pupiales"), Ciudad("Túquerres"), Ciudad("La Cruz"))),
        Departamento("Norte de Santander", listOf(Ciudad("Cúcuta"), Ciudad("Los Patios"), Ciudad("Villa del Rosario"), Ciudad("San Cayetano"), Ciudad("Ocaña"))),
        Departamento("Putumayo", listOf(Ciudad("Mocoa"), Ciudad("Sibundoy"), Ciudad("Puerto Asís"), Ciudad("Puerto Caicedo"), Ciudad("Orito"))),
        Departamento("Quindío", listOf(Ciudad("Armenia"), Ciudad("Calarcá"), Ciudad("Circasia"), Ciudad("Filandia"), Ciudad("Salento"), Ciudad("Pereira"))),
        Departamento("Risaralda", listOf(Ciudad("Pereira"), Ciudad("Dosquebradas"), Ciudad("Santa Rosa de Cabal"), Ciudad("La Virginia"), Ciudad("Quinchía"))),
        Departamento("Santander", listOf(Ciudad("Bucaramanga"), Ciudad("Floridablanca"), Ciudad("Girón"), Ciudad("Piedecuesta"), Ciudad("Barrancabermeja"), Ciudad("Socorro"))),
        Departamento("Sucre", listOf(Ciudad("Sincelejo"), Ciudad("Colosó"), Ciudad("Sampués"), Ciudad("Tolú"), Ciudad("Coveñas"))),
        Departamento("Tolima", listOf(Ciudad("Ibagué"), Ciudad("Espinal"), Ciudad("Melgar"), Ciudad("Guamo"), Ciudad("Algeciras"))),
        Departamento("Valle del Cauca", listOf(Ciudad("Cali"), Ciudad("Palmira"), Ciudad("Bubuey"), Ciudad("Cartago"), Ciudad("Buga"), Ciudad("Tuluá"), Ciudad("Yumbo"))),
        Departamento("Vaupés", listOf(Ciudad("Mitú"), Ciudad("Caruru"), Ciudad("Taraira"))),
        Departamento("Vichada", listOf(Ciudad("Puerto Carreño"), Ciudad("La Primavera"), Ciudad("Santa Rosalía"))),
        Departamento("Archipiélago de San Andrés", listOf(Ciudad("San Andrés"), Ciudad("Santa Catalina"), Ciudad("Providencia"))),
        Departamento("Distrito Capital", listOf(Ciudad("Bogotá D.C."), Ciudad("Usaquén"), Ciudad("Chapinero"), Ciudad("La Candelaria")))
    )

    // Métodos de conveniencia
    fun getDepartamentosPorNombre(): Map<String, Departamento> {
        return departamentos.associateBy { it.nombre }
    }

    fun getCiudadesPorDepartamento(nombreDepartamento: String): List<Ciudad> {
        return departamentos.find { it.nombre == nombreDepartamento }?.ciudades ?: emptyList()
    }

    fun getNombresDeparts(): List<String> {
        return departamentos.map { it.nombre }
    }

    fun getNombresCiudades(nombreDepartamento: String): List<String> {
        return getCiudadesPorDepartamento(nombreDepartamento).map { it.nombre }
    }
}

