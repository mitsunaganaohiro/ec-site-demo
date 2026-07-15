package com.example.demo.repository;

import com.example.demo.entity.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberRepository {

    /**
     * ソフトデリート済み会員を除いた有効な会員をメールアドレスで検索する。
     */
    Optional<Member> findByEmail(String email);

    /**
     * 会員を登録する。自動採番されたIDが引数のmemberにセットされる。
     */
    void insert(Member member);
}
