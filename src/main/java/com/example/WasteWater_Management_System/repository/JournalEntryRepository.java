package com.example.WasteWater_Management_System.repository;

import com.example.WasteWater_Management_System.models.JournalEntry;
import com.example.WasteWater_Management_System.models.enums.JournalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByJournalType(JournalType journalType);

    @Query("SELECT j FROM JournalEntry j WHERE j.debitAccount.id = :accountId OR j.creditAccount.id = :accountId")
    List<JournalEntry> findByAccountId(@Param("accountId") Long accountId);
}
