// Copyright (c) 2021 Aleksandr Minkin aka Rasalexman (sphc@yandex.ru)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package pro.krit.processor.annotations

/**
 * Main annotation for generating database implementation and used only with abstract class
 * [pro.krit.processor.provider.AbstractFmpDatabase] and his abstract extended children.
 * It's generate an Kotlin Object class with current implementation of abstract functions f
 * or provide instance of database DAO
 * @param asDaoProvider - need to create dao implementation object every time that it was called.
 * Default is single instance.
 *
 * Example:
 *
 * <p><pre>
 *  &#64FmpDatabase
 *  abstract class MainDatabase : AbstractFmpDatabase() {
 *      abstract fun providePmDao(): IPmDataDao
 *      abstract fun providePmLocalDao(): IPmDataLocalDao
 *  }
 *  </pre></p>
 *
 *  or with interface abstraction need to extend [pro.krit.processor.provider.IFmpDatabase]
 *  <p><pre>
 *  @FmpDatabase
 *  abstract class MainDatabase : AbstractFmpDatabase(), IMainDatabase
 *
 *  interface IMainDatabase : IFmpDatabase {
 *      fun providePmDao(): IPmDataDao
 *      fun providePmLocalDao(): IPmDataLocalDao
 *  }
 *  </pre></p>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FmpDatabase(
    val asDaoProvider: Boolean = false,
    val asSingleton: Boolean = true
)
