package com.lost.domain.metrics

/** Eventos instrumentados (sección 9.4 del plan). */
enum class MetricEventType {
    ALERT_SHOWN,
    USER_RESPONDED,
    ALERT_DISMISSED_IM_FINE,
    ALERT_DISMISSED_GOING_ELSEWHERE,
    ASSISTANCE_STARTED,
    ROUTE_RECOVERED,
    DESTINATION_REACHED,
    LOCATION_SHARED,
    ROUTINE_LEARNED,
    TRIP_COMPLETED
}
