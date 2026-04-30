package com.oeuvre.aether.model

import com.oeuvre.aether.location.LatLng

object SampleEvents {
    val list = listOf(
        Event(
            id = "1",
            name = "Eiffel Tower",
            description = "Iconic iron lattice tower on the Champ de Mars.",
            address = "Champ de Mars, 5 Av. Anatole France, 75007 Paris",
            location = LatLng(48.8584, 2.2945),
            category = "Landmark",
        ),
        Event(
            id = "2",
            name = "Louvre Museum",
            description = "World's largest art museum and a historic monument.",
            address = "Rue de Rivoli, 75001 Paris",
            location = LatLng(48.8606, 2.3376),
            category = "Museum",
        ),
        Event(
            id = "3",
            name = "Notre-Dame Cathedral",
            description = "Medieval Catholic cathedral on the Île de la Cité.",
            address = "6 Parvis Notre-Dame, 75004 Paris",
            location = LatLng(48.8530, 2.3499),
            category = "Landmark",
        ),
        Event(
            id = "4",
            name = "Musée d'Orsay",
            description = "French art museum housed in a Beaux-Arts railway station.",
            address = "1 Rue de la Légion d'Honneur, 75007 Paris",
            location = LatLng(48.8600, 2.3266),
            category = "Museum",
        ),
    )
}
