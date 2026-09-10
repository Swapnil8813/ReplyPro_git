package com.digitechno.replypro.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(

    entities = [

        SentMessage::class,

        PendingMessage::class

    ],

    version = 4,

    exportSchema = false

)

abstract class ReplyProDatabase : RoomDatabase() {

    abstract fun sentMessageDao(): SentMessageDao

    abstract fun pendingMessageDao(): PendingMessageDao

    companion object {
        private val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
            ALTER TABLE sent_messages
            ADD COLUMN messageType TEXT NOT NULL DEFAULT 'WHATSAPP'
            """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: ReplyProDatabase? = null

        fun get(context: Context): ReplyProDatabase {

            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    ReplyProDatabase::class.java,
                    "replypro.db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {

                        INSTANCE = it

                    }

            }

        }

    }

}