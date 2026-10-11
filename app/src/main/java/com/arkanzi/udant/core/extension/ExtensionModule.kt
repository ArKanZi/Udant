package com.arkanzi.udant.core.extension

import com.arkanzi.udant.core.model.UdantExtension
import com.arkanzi.udant.extension.thetimesofindia.TheTimesOfIndiaMain
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExtensionModule {

    @Binds
    @IntoSet
    abstract fun bindTheTimesOfIndia(
        extension: TheTimesOfIndiaMain
    ): UdantExtension
//
//    @Binds
//    @IntoSet
//    abstract fun bindTheIndianExpress(
//        extension: TheIndianExpress
//    ): UdantExtension
}