package com.lost.watch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.lost.watch.feature.demo.DemoScreen
import com.lost.watch.feature.insights.InsightsScreen
import com.lost.watch.feature.insights.MetricsScreen
import com.lost.watch.feature.learning.LearningScreen
import com.lost.watch.feature.monitoring.ExplanationScreen
import com.lost.watch.feature.monitoring.MonitoringScreen
import com.lost.watch.feature.newplace.NewPlaceGuideScreen
import com.lost.watch.feature.newplace.NewPlaceScreen
import com.lost.watch.feature.onboarding.ContactSetupScreen
import com.lost.watch.feature.onboarding.LocationPermissionScreen
import com.lost.watch.feature.onboarding.WelcomeScreen
import com.lost.watch.feature.routines.RoutineDetailScreen
import com.lost.watch.feature.settings.PrivacyScreen
import com.lost.watch.feature.settings.SensitivityScreen

/**
 * Navegación local: `SwipeDismissableNavHost` (deslizar a la derecha = volver).
 * Nunca más de dos niveles bajo las secciones globales.
 */
@Composable
fun LostNavHost(isOnboardingCompleted: Boolean) {
    val navController = rememberSwipeDismissableNavController()
    val navigator = remember(navController) { LostNavigator(navController) }
    val start = if (isOnboardingCompleted) LostDestination.Sections else LostDestination.Welcome
    SwipeDismissableNavHost(navController = navController, startDestination = start.route) {
        onboardingRoutes(navigator)
        composable(LostDestination.Sections.route) { SectionsPager(navigator) }
        monitoringRoutes(navigator)
        settingsRoutes(navigator)
        composable(LostDestination.Demo.route) { DemoScreen(onStarted = navigator::backToSections) }
        composable(LostDestination.PlaceGuide.route) { NewPlaceGuideScreen(onFinished = navigator::backToSections) }
        composable(LostDestination.RoutineDetail.route) { RoutineDetailScreen(onDeleted = navigator::back) }
        composable(LostDestination.Learning.route) { LearningScreen() }
        composable(LostDestination.NewPlace.route) {
            NewPlaceScreen(onStarted = { navigator.open(LostDestination.PlaceGuide) })
        }
    }
}

private fun NavGraphBuilder.onboardingRoutes(navigator: LostNavigator) {
    composable(LostDestination.Welcome.route) {
        WelcomeScreen(onStart = { navigator.open(LostDestination.LocationPermission) })
    }
    composable(LostDestination.LocationPermission.route) {
        LocationPermissionScreen(onContinue = { navigator.open(LostDestination.ContactSetup) })
    }
    composable(LostDestination.ContactSetup.route) { ContactSetupScreen(onFinished = navigator::finishOnboarding) }
}

private fun NavGraphBuilder.monitoringRoutes(navigator: LostNavigator) {
    composable(LostDestination.Monitoring.route) {
        MonitoringScreen(onOpenExplanation = { navigator.open(LostDestination.Explanation) })
    }
    composable(LostDestination.Explanation.route) { ExplanationScreen() }
}

private fun NavGraphBuilder.settingsRoutes(navigator: LostNavigator) {
    composable(LostDestination.Insights.route) {
        InsightsScreen(onOpenMetrics = { navigator.open(LostDestination.Metrics) })
    }
    composable(LostDestination.Metrics.route) { MetricsScreen() }
    composable(LostDestination.Sensitivity.route) { SensitivityScreen() }
    composable(LostDestination.Privacy.route) { PrivacyScreen() }
}
