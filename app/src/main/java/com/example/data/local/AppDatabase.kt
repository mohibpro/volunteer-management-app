package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        OrganizationEntity::class,
        OpportunityEntity::class,
        ApplicationEntity::class,
        SavedOpportunityEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun organizationDao(): OrganizationDao
    abstract fun opportunityDao(): OpportunityDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun savedOpportunityDao(): SavedOpportunityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "servesync_pk_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val userDao = database.userDao()
            val orgDao = database.organizationDao()
            val oppDao = database.opportunityDao()
            val appDao = database.applicationDao()

            // Seed users
            userDao.insertUser(SeedData.defaultVolunteer)
            userDao.insertUser(SeedData.defaultOrganizer)

            // Seed organizations
            SeedData.sampleOrganizations.forEach { org ->
                orgDao.insertOrganization(org)
            }

            // Seed opportunities
            SeedData.sampleOpportunities.forEach { opp ->
                oppDao.insertOpportunity(opp)
            }

            // Seed applications
            SeedData.sampleApplications.forEach { app ->
                appDao.insertApplication(app)
            }
        }
    }
}
