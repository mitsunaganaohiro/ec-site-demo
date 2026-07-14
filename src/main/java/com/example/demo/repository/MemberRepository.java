package com.example.demo.repository;

import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    /**
     * ソフトデリート済み会員を除いた有効な会員をメールアドレスで検索する。
     * membersテーブルは (email, deleted_key) の複合UNIQUE制約により、
     * 退会済み会員のメールアドレスは再利用可能なため、単純なfindByEmailは使わない。
     */
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);
}
