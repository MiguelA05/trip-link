package com.example.triplink.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplink.domain.model.Publication

@Composable
fun PublicationList(modifier: Modifier = Modifier) {
    val publications = listOf(
        Publication(
            id = "1",
            title = "Explorando Nueva York",
            description = "Un recorrido por la Gran Manzana, desde Central Park hasta Times Square.",
            imageUrl = "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "2",
            title = "Aventura en los Alpes",
            description = "Senderismo y paisajes inolvidables en el corazón de Europa.",
            imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "3",
            title = "Playas de Cancún",
            description = "Relajación total en las aguas cristalinas del Caribe mexicano.",
            imageUrl = "https://images.unsplash.com/photo-1510414842594-a61c69b5ae57?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "4",
            title = "Cultura en Kioto",
            description = "Templos milenarios y la esencia tradicional de Japón.",
            imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "5",
            title = "Safari en Kenia",
            description = "Observando la majestuosa fauna salvaje en su hábitat natural.",
            imageUrl = "https://images.unsplash.com/photo-1516422317184-268a73a3ad9a?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "6",
            title = "Luces de París",
            description = "La ciudad del amor y su inigualable arquitectura histórica.",
            imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "7",
            title = "Maravillas de Machu Picchu",
            description = "Descubriendo la ciudad perdida de los Incas en los Andes.",
            imageUrl = "https://images.unsplash.com/photo-1526392060635-9d6019884377?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "8",
            title = "Rascacielos de Dubái",
            description = "Lujo y modernidad en el desierto de los Emiratos Árabes.",
            imageUrl = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "9",
            title = "Historia en Roma",
            description = "Un viaje al pasado a través del Coliseo y el Foro Romano.",
            imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "10",
            title = "Naturaleza en Islandia",
            description = "Cascadas, glaciares y auroras boreales en un solo lugar.",
            imageUrl = "https://images.unsplash.com/photo-1476610182048-b716b8518aae?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "11",
            title = "Gran Cañón, Arizona",
            description = "Vistas impresionantes de uno de los desfiladeros más profundos del mundo.",
            imageUrl = "https://images.unsplash.com/photo-1474044159687-1ee9f3a51722?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "12",
            title = "Santorini, Grecia",
            description = "Casas blancas y cúpulas azules con vistas al mar Egeo.",
            imageUrl = "https://images.unsplash.com/photo-1469041412761-45c94b21404d?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "13",
            title = "Sídney, Australia",
            description = "La famosa Ópera de Sídney y el puente de la bahía.",
            imageUrl = "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "14",
            title = "Londres, Reino Unido",
            description = "Paseando por el Támesis con vistas al Big Ben y el London Eye.",
            imageUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "15",
            title = "Río de Janeiro, Brasil",
            description = "Desde el Cristo Redentor hasta la vibrante playa de Copacabana.",
            imageUrl = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "16",
            title = "Estambul, Turquía",
            description = "Donde Europa se encuentra con Asia en el Bósforo.",
            imageUrl = "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "17",
            title = "Ámsterdam, Países Bajos",
            description = "Canales históricos y museos de clase mundial.",
            imageUrl = "https://images.unsplash.com/photo-1512470876302-972faa2aa9a4?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "18",
            title = "Ciudad del Cabo, Sudáfrica",
            description = "La Montaña de la Mesa dominando el paisaje urbano.",
            imageUrl = "https://images.unsplash.com/photo-1580619305218-8423a7f79b3f?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "19",
            title = "Praga, República Checa",
            description = "Arquitectura gótica y el icónico Puente Carlos.",
            imageUrl = "https://images.unsplash.com/photo-1541849546-216549ae216d?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "20",
            title = "Bali, Indonesia",
            description = "Playas paradisíacas, templos y campos de arroz infinitos.",
            imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "21",
            title = "Barcelona, España",
            description = "La genialidad de Gaudí en la Sagrada Familia y el Parque Güell.",
            imageUrl = "https://images.unsplash.com/photo-1583997051651-896749e71cb7?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "22",
            title = "Venecia, Italia",
            description = "Paseos en góndola por los canales más románticos del mundo.",
            imageUrl = "https://images.unsplash.com/photo-1514890547357-a9ee2887ad8e?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "23",
            title = "El Cairo, Egipto",
            description = "Las milenarias Pirámides de Giza y la Gran Esfinge.",
            imageUrl = "https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "24",
            title = "San Francisco, USA",
            description = "El Puente Golden Gate envuelto en la clásica niebla.",
            imageUrl = "https://images.unsplash.com/photo-1449034446853-66c86144b0ad?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "25",
            title = "Berlín, Alemania",
            description = "Historia moderna, arte callejero y una vibrante vida nocturna.",
            imageUrl = "https://images.unsplash.com/photo-1528728329032-2972f65dfb3f?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "26",
            title = "Lisboa, Portugal",
            description = "Tranvías amarillos y vistas panorámicas desde sus colinas.",
            imageUrl = "https://images.unsplash.com/photo-1548120231-dddec4144081?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "27",
            title = "Ciudad de México",
            description = "Cultura, gastronomía y el Palacio de Bellas Artes.",
            imageUrl = "https://images.unsplash.com/photo-1518105779142-d975f22f1b0a?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "28",
            title = "Seúl, Corea del Sur",
            description = "Modernidad futurista y palacios reales tradicionales.",
            imageUrl = "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "29",
            title = "Atenas, Grecia",
            description = "Cuna de la civilización occidental en el Partenón.",
            imageUrl = "https://images.unsplash.com/photo-1503152394-c571994fd383?q=80&w=1000&auto=format&fit=crop"
        ),
        Publication(
            id = "30",
            title = "Bangkok, Tailandia",
            description = "Templos dorados y la energía incansable de la ciudad.",
            imageUrl = "https://images.unsplash.com/photo-1504609770332-954fd45261e9?q=80&w=1000&auto=format&fit=crop"
        )
    )

    //Column comun no es viable porque renderiza todo y gasta demaciados recursos
    //LazyColumn carga por demanda
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(publications) { publication ->
            PublicationCard(publication = publication)
        }
    }
}
