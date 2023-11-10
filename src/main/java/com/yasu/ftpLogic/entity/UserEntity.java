package com.yasu.ftpLogic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "u_name")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "folder_path")
    private String folder;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    @Column(name = "pwd")
    private String password;
    public UserEntity(String username,String email,String password){
        this.username=username;
        this.email=email;
        this.password=password;
    }



}
