/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.xlei.vipexam.core.data.di

import app.xlei.vipexam.core.data.repository.Repository
import app.xlei.vipexam.core.data.repository.UserRepository
import app.xlei.vipexam.core.data.repository.WordRepository
import app.xlei.vipexam.core.data.util.ConnectivityManagerNetworkMonitor
import app.xlei.vipexam.core.data.util.NetworkMonitor
import app.xlei.vipexam.core.database.module.User
import app.xlei.vipexam.core.database.module.Word
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsWordRepository(
        wordRepository: WordRepository
    ): Repository<Word>

    @Binds
    fun bindsUserRepository(
        userRepository: UserRepository
    ): Repository<User>

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}