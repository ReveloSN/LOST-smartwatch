package com.lost.watch.navigation

import androidx.navigation.NavHostController

/** Acciones de navegación que reciben las pantallas como lambdas (ninguna pantalla conoce el NavController). */
class LostNavigator(private val navController: NavHostController) {
    fun back() {
        navController.popBackStack()
    }

    fun open(destination: LostDestination) = navController.navigate(destination.route)

    fun openRoutine(routineId: String) = navController.navigate(LostDestination.RoutineDetail.to(routineId))

    fun openLearning(routineId: String) = navController.navigate(LostDestination.Learning.to(routineId))

    fun openNewPlace(destinationId: String) = navController.navigate(LostDestination.NewPlace.to(destinationId))

    fun finishOnboarding() = navController.navigate(LostDestination.Sections.route) {
        popUpTo(navController.graph.id) { inclusive = true }
    }

    fun backToSections() {
        navController.popBackStack(LostDestination.Sections.route, inclusive = false)
    }
}
