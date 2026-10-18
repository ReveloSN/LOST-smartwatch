package com.lost.watch.navigation

/**
 * Todas las rutas de navegación de LOST (sistema de navegación de la arquitectura de información).
 * Jerarquía plana: las 5 secciones globales viven en [Sections]; el resto es como máximo un nivel más.
 */
sealed interface LostDestination {
    val route: String

    data object Welcome : LostDestination {
        override val route = "onboarding/welcome"
    }

    data object LocationPermission : LostDestination {
        override val route = "onboarding/location"
    }

    data object ContactSetup : LostDestination {
        override val route = "onboarding/contact"
    }

    data object Sections : LostDestination {
        override val route = "sections"
    }

    data object Monitoring : LostDestination {
        override val route = "monitoring"
    }

    data object Explanation : LostDestination {
        override val route = "explanation"
    }

    data object Insights : LostDestination {
        override val route = "insights"
    }

    data object Sensitivity : LostDestination {
        override val route = "settings/sensitivity"
    }

    data object Privacy : LostDestination {
        override val route = "settings/privacy"
    }

    data object Demo : LostDestination {
        override val route = "demo"
    }

    data object PlaceGuide : LostDestination {
        override val route = "newplace/guide"
    }

    data object RoutineDetail : LostDestination {
        const val ARG_ROUTINE_ID = "routineId"
        override val route = "routine/{$ARG_ROUTINE_ID}"

        fun to(routineId: String) = "routine/$routineId"
    }

    data object Learning : LostDestination {
        const val ARG_ROUTINE_ID = "routineId"
        override val route = "learning/{$ARG_ROUTINE_ID}"

        fun to(routineId: String) = "learning/$routineId"
    }

    data object NewPlace : LostDestination {
        const val ARG_DESTINATION_ID = "destinationId"
        override val route = "newplace/{$ARG_DESTINATION_ID}"

        fun to(destinationId: String) = "newplace/$destinationId"
    }
}
