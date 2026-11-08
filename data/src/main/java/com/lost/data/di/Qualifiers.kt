package com.lost.data.di

import javax.inject.Qualifier

/** DataStore de las preferencias del usuario. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsStore

/** DataStore del contacto autorizado. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ContactStore

/** Alcance de corrutinas que vive lo mismo que la aplicación. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

/** Despachador para lecturas de red y de disco. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher
