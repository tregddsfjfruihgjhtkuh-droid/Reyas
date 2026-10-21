package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Announcement
import com.example.data.model.Favorite
import com.example.data.model.Like
import com.example.data.model.Notification
import com.example.data.model.Report
import com.example.data.model.TexturePack
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        TexturePack::class,
        Report::class,
        Favorite::class,
        Like::class,
        Announcement::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): ReyasNovaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reyasnova_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial seed data on background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context).dao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(dao: ReyasNovaDao) {
            // Seed Users
            val userReyas = User(
                id = "usr_reyasnova",
                username = "reyasnova",
                displayName = "ReyasNova",
                email = "reyas@reyasnova.com",
                avatarUrl = "",
                role = User.ROLE_USER,
                accountStatus = User.STATUS_ACTIVE,
                bio = "Official texture pack creator & community lead. Crafting crisp PvP & smooth UI resource packs."
            )
            val userAdmin = User(
                id = "usr_admin",
                username = "admin",
                displayName = "Platform Owner",
                email = "admin@reyasnova.com",
                avatarUrl = "",
                role = User.ROLE_ADMIN,
                accountStatus = User.STATUS_ACTIVE,
                bio = "ReyasNova Head Moderator & Security Lead"
            )
            val userSkyCraft = User(
                id = "usr_skycraft",
                username = "skycraft",
                displayName = "SkyCraft",
                email = "skycraft@gmail.com",
                role = User.ROLE_USER,
                bio = "UI & HUD designer for Minecraft Java & Bedrock"
            )
            val userNova = User(
                id = "usr_novatextures",
                username = "novatextures",
                displayName = "NovaTextures",
                email = "nova@novatextures.net",
                role = User.ROLE_USER,
                bio = "Faithful 32x and 64x texture packs"
            )
            val userZack = User(
                id = "usr_zackpack",
                username = "zackpack",
                displayName = "ZackPack",
                email = "zack@zackpack.io",
                role = User.ROLE_USER,
                bio = "Specializing in custom animated swords, potions, and armor"
            )

            dao.insertUser(userReyas)
            dao.insertUser(userAdmin)
            dao.insertUser(userSkyCraft)
            dao.insertUser(userNova)
            dao.insertUser(userZack)

            // Seed Texture Packs
            val pack1 = TexturePack(
                id = "pack_purple_pvp_16x",
                creatorId = "usr_reyasnova",
                creatorName = "ReyasNova",
                name = "Purple PvP 16x",
                description = "A clean and smooth PvP texture pack with animated glowing items, custom purple GUI, low fire, short swords, and high FPS optimization.",
                version = "1.4.0",
                minecraftVersion = "1.21.1",
                resolution = "16x",
                category = "PvP",
                tags = "pvp, 16x, purple, smooth, animated, low fire",
                fileName = "Purple_PvP_16x_v1.4.zip",
                fileSizeBytes = 18_450_000L,
                downloads = 45800,
                likes = 12400,
                status = TexturePack.STATUS_APPROVED,
                isFeatured = true,
                changelog = "v1.4: Updated for MC 1.21.1, new Mace textures, custom glowing Ender Pearl animations."
            )

            val pack2 = TexturePack(
                id = "pack_sky_blue_ui",
                creatorId = "usr_skycraft",
                creatorName = "SkyCraft",
                name = "Sky Blue UI",
                description = "Modern translucent sky blue interface pack replacing inventory, hotbar, chest menus, and status icons with high-contrast glass textures.",
                version = "2.1.0",
                minecraftVersion = "1.21.1",
                resolution = "32x",
                category = "UI",
                tags = "ui, 32x, blue, modern, clean, glass",
                fileName = "Sky_Blue_UI_32x.zip",
                fileSizeBytes = 14_200_000L,
                downloads = 32100,
                likes = 8700,
                status = TexturePack.STATUS_APPROVED,
                isFeatured = true,
                changelog = "v2.1: Revamped dark mode dialogs and furnace cooking progress indicators."
            )

            val pack3 = TexturePack(
                id = "pack_dark_faithful",
                creatorId = "usr_novatextures",
                creatorName = "NovaTextures",
                name = "Dark Faithful",
                description = "The classic Faithful 64x resource pack enriched with dark obsidian tones, high-definition block details, and reduced eye strain for night gaming.",
                version = "3.0.2",
                minecraftVersion = "1.20.6",
                resolution = "64x",
                category = "Faithful",
                tags = "faithful, 64x, vanilla, hd, dark",
                fileName = "Dark_Faithful_64x.zip",
                fileSizeBytes = 38_700_000L,
                downloads = 24400,
                likes = 6200,
                status = TexturePack.STATUS_APPROVED,
                isFeatured = true,
                changelog = "v3.0.2: Upscaled cherry wood textures and wolf armor variants."
            )

            val pack4 = TexturePack(
                id = "pack_animated_items",
                creatorId = "usr_zackpack",
                creatorName = "ZackPack",
                name = "Animated Items",
                description = "Brings every tool, sword, potion, and bucket to life with custom 60fps frame animations, pulsing enchantment glints, and particle effects.",
                version = "1.2.0",
                minecraftVersion = "1.21.1",
                resolution = "16x",
                category = "Animated",
                tags = "animated, items, 16x, potions, glowing, particles",
                fileName = "Animated_Items_16x.zip",
                fileSizeBytes = 22_100_000L,
                downloads = 38900,
                likes = 10100,
                status = TexturePack.STATUS_APPROVED,
                isFeatured = true,
                changelog = "v1.2: Added glowing animations for Netherite ingot, Totem of Undying, and Wind Charge."
            )

            val pack5 = TexturePack(
                id = "pack_bedwars_pro_16x",
                creatorId = "usr_reyasnova",
                creatorName = "ReyasNova",
                name = "Bedwars Pro 16x",
                description = "Ultra-optimized Bedwars resource pack with highlighted wool colors, clear glass, distinct clay textures, and audible low-latency hit sounds.",
                version = "2.0.0",
                minecraftVersion = "1.8.9",
                resolution = "16x",
                category = "Bedwars",
                tags = "bedwars, 16x, pvp, hypixel, competitive",
                fileName = "Bedwars_Pro_16x.zip",
                fileSizeBytes = 11_300_000L,
                downloads = 18200,
                likes = 4300,
                status = TexturePack.STATUS_APPROVED,
                isFeatured = false,
                changelog = "v2.0: Added custom diamond sword gradient and diamond armor outline."
            )

            val pack6 = TexturePack(
                id = "pack_pending_lowfire",
                creatorId = "usr_reyasnova",
                creatorName = "ReyasNova",
                name = "Ender Dragon Low Fire",
                description = "Minimalist purple low fire pack designed for UHC and Bedwars matches, providing maximum screen visibility in lava and flame combat.",
                version = "1.0.0",
                minecraftVersion = "1.21.1",
                resolution = "16x",
                category = "Low Fire",
                tags = "low fire, pvp, purple, uhc",
                fileName = "Ender_Low_Fire_16x.zip",
                fileSizeBytes = 8_900_000L,
                downloads = 0,
                likes = 0,
                status = TexturePack.STATUS_PENDING,
                isFeatured = false,
                changelog = "Initial submission awaiting staff review."
            )

            dao.insertPack(pack1)
            dao.insertPack(pack2)
            dao.insertPack(pack3)
            dao.insertPack(pack4)
            dao.insertPack(pack5)
            dao.insertPack(pack6)

            // Seed Announcement
            dao.insertAnnouncement(
                Announcement(
                    id = "ann_1",
                    title = "⚔️ Summer Texture Pack Contest Is Live!",
                    content = "Upload your custom 16x or 32x PvP and UI packs to win the Verified Creator badge and top featured spotlight!",
                    badge = "CONTEST"
                )
            )

            // Seed Report for Admin Moderation demo
            dao.insertReport(
                Report(
                    id = "rep_1",
                    reporterId = "usr_skycraft",
                    reporterName = "SkyCraft",
                    targetType = Report.TYPE_PACK,
                    targetId = "pack_purple_pvp_16x",
                    targetTitle = "Purple PvP 16x",
                    reason = "Broken / Corrupted Download",
                    adminNotes = "User reported missing particles on Minecraft 1.21.0 hotfix.",
                    status = Report.STATUS_OPEN
                )
            )

            // Seed Likes and Favorites for reyasnova
            dao.insertFavorite(Favorite(userId = "usr_reyasnova", packId = "pack_purple_pvp_16x"))
            dao.insertFavorite(Favorite(userId = "usr_reyasnova", packId = "pack_sky_blue_ui"))
            dao.insertLike(Like(userId = "usr_reyasnova", packId = "pack_purple_pvp_16x"))
        }
    }
}
