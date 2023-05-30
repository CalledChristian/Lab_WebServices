package com.example.labwebservice.Repository;

import com.example.labwebservice.Entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character,Integer> {
}
