package br.com.ss.blog.infra.cache;

public final class CacheNames {
    private CacheNames() {}

    // Define a versão atual do esquema de cache. Incremente este número em cada alteração de estrutura de DTO.
    public static final String CACHE_VERSION = "v1";

    public static final String USERS = "users_" + CACHE_VERSION;
}