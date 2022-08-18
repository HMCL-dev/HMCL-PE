project(Hin2n)
cmake_minimum_required(VERSION 3.4.1)

# N2n information
set(N2N_VERSION 2.9.0)
set(N2N_OSNAME ${CMAKE_SYSTEM})
set(N2N_MODIFY_VERSION v2s_0.1.0)
set(N2N_MODIFY_AUTHOR "switchwang(https://github.com/switch-st) zhangbz(https://github.com/zhangbz)")
add_definitions(-DCMAKE_BUILD)

INCLUDE(TestBigEndian)
TEST_BIG_ENDIAN(UIP_CONF_BYTE_ORDER)
if(${UIP_CONF_BYTE_ORDER} EQUAL 1)
set(UIP_CONF_BYTE_ORDER UIP_BIG_ENDIAN)
else(${UIP_CONF_BYTE_ORDER} EQUAL 1)
set(UIP_CONF_BYTE_ORDER UIP_LITTLE_ENDIAN)
endif(${UIP_CONF_BYTE_ORDER} EQUAL 1)

# OpenSSL
SET(OPENSSL_ROOT_DIR src/main/jniLibs/${ANDROID_ABI})
SET(OPENSSL_LIBRARIES_DIR "${OPENSSL_ROOT_DIR}")
SET(OPENSSL_INCLUDE_DIR ${OPENSSL_ROOT_DIR}/include)
SET(OPENSSL_LIBRARIES "crypto")
LINK_DIRECTORIES(${OPENSSL_LIBRARIES_DIR})
OPTION(N2N_OPTION_AES "USE AES" ON)
set(N2N_OPTION_AES ON)

if(CMAKE_BUILD_TYPE)
if(NOT ${CMAKE_BUILD_TYPE} STREQUAL Debug)
set(CMAKE_BUILD_TYPE Release)
endif(NOT ${CMAKE_BUILD_TYPE} STREQUAL Debug)
else(CMAKE_BUILD_TYPE)
set(CMAKE_BUILD_TYPE Release)
endif(CMAKE_BUILD_TYPE)
add_definitions(-DCMAKE_BUILD)

add_definitions(-DN2N_VERSION=\"${N2N_VERSION}\" -DN2N_OSNAME=\"${N2N_OSNAME}\" -DN2N_MODIFY_VERSION=\"${N2N_MODIFY_VERSION}\" -DN2N_MODIFY_AUTHOR=\"${N2N_MODIFY_AUTHOR}\" -DUIP_CONF_BYTE_ORDER=\"${UIP_CONF_BYTE_ORDER}\")
add_definitions(-DGIT_RELEASE="" -DPACKAGE_VERSION="${N2N_VERSION}" -DPACKAGE_OSNAME="${CMAKE_SYSTEM}")
include_directories(src/main/cpp src/main/cpp/edge_jni src/main/cpp/slog src/main/cpp/tun2tap src/main/cpp/uip)

add_library(edge_jni SHARED src/main/cpp/edge_jni/edge_jni.c)
target_link_libraries(edge_jni edge_v3)
target_link_libraries(edge_jni edge_v2s)
target_link_libraries(edge_jni edge_v2)
target_link_libraries(edge_jni edge_v1)
target_link_libraries(edge_jni slog)
target_link_libraries(edge_jni ${OPENSSL_LIBRARIES})

add_library(edge_v2s SHARED src/main/cpp/n2n_v2s/edge.c)
target_link_libraries(edge_v2s n2n_v2s)
target_link_libraries(edge_v2s slog)

# -- ntop n2n
add_library(edge_v2 SHARED
            src/main/cpp/n2n_v2/src/n2n.c
            src/main/cpp/n2n_v2/src/edge_utils.c
            src/main/cpp/n2n_v2/src/wire.c
            src/main/cpp/n2n_v2/src/minilzo.c
            src/main/cpp/n2n_v2/src/twofish.c
            src/main/cpp/n2n_v2/src/speck.c
            src/main/cpp/n2n_v2/src/pearson.c
            src/main/cpp/n2n_v2/src/header_encryption.c
            src/main/cpp/n2n_v2/src/transform_null.c
            src/main/cpp/n2n_v2/src/transform_tf.c
            src/main/cpp/n2n_v2/src/transform_aes.c
            src/main/cpp/n2n_v2/src/transform_speck.c
            src/main/cpp/n2n_v2/src/transform_cc20.c
            src/main/cpp/n2n_v2/src/random_numbers.c

            src/main/cpp/edge_jni/tuntap_android_v2.c
            src/main/cpp/edge_jni/edge_android_v2.c
        )
target_compile_definitions(edge_v2 PUBLIC N2N_HAVE_AES HAVE_OPENSSL_1_1)
target_include_directories(edge_v2 PUBLIC
            src/main/cpp/n2n_v2/include
            ${OPENSSL_INCLUDE_DIR}
        )
target_link_libraries(edge_v2 uip)
target_link_libraries(edge_v2 ${OPENSSL_LIBRARIES})

add_library(edge_v3 SHARED
        src/main/cpp/n2n_v3/src/n2n.c
        src/main/cpp/n2n_v3/src/edge_management.c
        src/main/cpp/n2n_v3/src/curve25519.c
        src/main/cpp/n2n_v3/src/auth.c
        src/main/cpp/n2n_v3/src/edge_utils.c
        src/main/cpp/n2n_v3/src/wire.c
        src/main/cpp/n2n_v3/src/minilzo.c
        src/main/cpp/n2n_v3/src/speck.c
        src/main/cpp/n2n_v3/src/pearson.c
        src/main/cpp/n2n_v3/src/tf.c
        src/main/cpp/n2n_v3/src/header_encryption.c
        src/main/cpp/n2n_v3/src/transform_null.c
        src/main/cpp/n2n_v3/src/transform_tf.c
        src/main/cpp/n2n_v3/src/transform_aes.c
        src/main/cpp/n2n_v3/src/transform_speck.c
        src/main/cpp/n2n_v3/src/transform_cc20.c
        src/main/cpp/n2n_v3/src/cc20.c
        src/main/cpp/n2n_v3/src/aes.c
        src/main/cpp/n2n_v3/src/network_traffic_filter.c
        src/main/cpp/n2n_v3/src/sn_selection.c
        src/main/cpp/n2n_v3/src/random_numbers.c
        src/main/cpp/edge_jni/tuntap_android_v2.c
        src/main/cpp/edge_jni/edge_android_v2.c
        )
target_compile_definitions(edge_v3 PUBLIC N2N_HAVE_AES HAVE_OPENSSL_1_1)
target_compile_options(edge_v3 PRIVATE -DN2N_V3)
target_include_directories(edge_v3 PUBLIC
        src/main/cpp/n2n_v3/include
        ${OPENSSL_INCLUDE_DIR}
        )
target_link_libraries(edge_v3 uip)
target_link_libraries(edge_v3 ${OPENSSL_LIBRARIES})

add_library(edge_v1 SHARED
                src/main/cpp/n2n_v1/edge.c
            )
target_link_libraries(edge_v1 n2n_v1)
target_link_libraries(edge_v1 slog)

add_library(n2n_v2s SHARED
                src/main/cpp/n2n_v2s/n2n.c
                src/main/cpp/n2n_v2s/n2n_keyfile.c
                src/main/cpp/n2n_v2s/wire.c
                src/main/cpp/n2n_v2s/minilzo.c
                src/main/cpp/n2n_v2s/twofish.c
                src/main/cpp/n2n_v2s/transform_null.c
                src/main/cpp/n2n_v2s/transform_tf.c
                src/main/cpp/n2n_v2s/transform_aes.c
                src/main/cpp/n2n_v2s/android/tuntap_android.c
                src/main/cpp/n2n_v2s/version.c
            )
target_link_libraries(n2n_v2s uip)
target_link_libraries(n2n_v2s slog)

add_library(n2n_v1 SHARED
                src/main/cpp/n2n_v1/n2n.c
                src/main/cpp/n2n_v1/minilzo.c
                src/main/cpp/n2n_v1/twofish.c
                src/main/cpp/n2n_v1/android/tuntap_android.c
                src/main/cpp/n2n_v1/version.c
            )
target_link_libraries(n2n_v1 uip)
target_link_libraries(n2n_v1 slog)

add_library(uip SHARED
                src/main/cpp/uip/uip.c
                src/main/cpp/uip/uip_arp.c
                src/main/cpp/tun2tap/tun2tap.c
            )

add_library(slog SHARED src/main/cpp/slog/slog.c)
target_link_libraries(slog log)
