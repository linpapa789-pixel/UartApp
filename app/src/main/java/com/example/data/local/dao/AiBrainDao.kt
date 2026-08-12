package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AiBrainVersionEntity
import com.example.data.local.entity.AiKeywordEntity
import com.example.data.local.entity.AiMemoryEntity
import com.example.data.local.entity.AiPromptEntity
import com.example.data.local.entity.AiRepairKnowledgeEntity
import com.example.data.local.entity.AiRoleEntity
import com.example.data.local.entity.AiRuleEntity
import com.example.data.local.entity.AiSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiBrainDao {

    // PROMPTS
    @Query("SELECT * FROM ai_prompts ORDER BY isFavorite DESC, priority ASC, id DESC")
    fun getAllPrompts(): Flow<List<AiPromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: AiPromptEntity): Long

    @Update
    suspend fun updatePrompt(prompt: AiPromptEntity)

    @Delete
    suspend fun deletePrompt(prompt: AiPromptEntity)

    // ROLES
    @Query("SELECT * FROM ai_roles ORDER BY isEnabled DESC, id ASC")
    fun getAllRoles(): Flow<List<AiRoleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: AiRoleEntity): Long

    @Update
    suspend fun updateRole(role: AiRoleEntity)

    @Delete
    suspend fun deleteRole(role: AiRoleEntity)

    // RULES
    @Query("SELECT * FROM ai_rules ORDER BY priority ASC, id DESC")
    fun getAllRules(): Flow<List<AiRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AiRuleEntity): Long

    @Update
    suspend fun updateRule(rule: AiRuleEntity)

    @Delete
    suspend fun deleteRule(rule: AiRuleEntity)

    // KEYWORDS
    @Query("SELECT * FROM ai_keywords ORDER BY keyword ASC")
    fun getAllKeywords(): Flow<List<AiKeywordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: AiKeywordEntity): Long

    @Update
    suspend fun updateKeyword(keyword: AiKeywordEntity)

    @Delete
    suspend fun deleteKeyword(keyword: AiKeywordEntity)

    // REPAIR KNOWLEDGE
    @Query("SELECT * FROM ai_repair_knowledge ORDER BY id DESC")
    fun getAllRepairKnowledge(): Flow<List<AiRepairKnowledgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairKnowledge(item: AiRepairKnowledgeEntity): Long

    @Update
    suspend fun updateRepairKnowledge(item: AiRepairKnowledgeEntity)

    @Delete
    suspend fun deleteRepairKnowledge(item: AiRepairKnowledgeEntity)

    // MEMORIES
    @Query("SELECT * FROM ai_memories ORDER BY priority ASC, id DESC")
    fun getAllMemories(): Flow<List<AiMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: AiMemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: AiMemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: AiMemoryEntity)

    // SETTINGS
    @Query("SELECT * FROM ai_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AiSettingsEntity?>

    @Query("SELECT * FROM ai_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AiSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AiSettingsEntity)

    // VERSIONS
    @Query("SELECT * FROM ai_brain_versions ORDER BY id DESC")
    fun getAllVersions(): Flow<List<AiBrainVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: AiBrainVersionEntity): Long

    @Delete
    suspend fun deleteVersion(version: AiBrainVersionEntity)
}
