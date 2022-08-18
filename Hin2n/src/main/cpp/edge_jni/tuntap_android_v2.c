/*
 * (C) 2007-09 - Luca Deri <deri@ntop.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>
*/

#include "n2n.h"

#ifdef __ANDROID_NDK__
#include <tun2tap/tun2tap.h>

/* ********************************** */

/** @brief  Open and configure the TAP device for packet read/write.
 *
 *  This routine creates the interface via the tuntap driver then uses ifconfig
 *  to configure address/mask and MTU.
 *
 *  @param device      - [inout] a device info holder object
 *  @param dev         - user-defined name for the new iface, 
 *                       if NULL system will assign a name
 *  @param device_ip   - address of iface
 *  @param device_mask - netmask for device_ip
 *  @param mtu         - MTU for device_ip
 *
 *  @return - negative value on error
 *          - non-negative file-descriptor on success
 */
int tuntap_open(tuntap_dev *device, 
                char *dev, /* user-definable interface name, eg. edge0 */
                const char *address_mode, /* static or dhcp */
                char *device_ip, 
                char *device_mask,
                const char * device_mac,
		        int mtu) {
    int i, n_matched;
    unsigned int mac[6];

    n_matched = sscanf(device_mac, "%x:%x:%x:%x:%x:%x", mac, mac + 1, mac + 2, mac + 3, mac + 4, mac + 5);
    if (n_matched != 6) {
        return -1;
    }
    memset(device->mac_addr, 0, sizeof(device->mac_addr));
    for (i = 0; i < 6; i++)
        device->mac_addr[i] = mac[i];
    device->ip_addr = inet_addr(device_ip);
    device->device_mask = inet_addr(device_mask);
    device->mtu = mtu;
    strncpy(device->dev_name, dev, N2N_IFNAMSIZ);
    return device->fd;
}

int tuntap_read(struct tuntap_dev *tuntap, unsigned char *buf, int len) {
    memset(buf, 0, UIP_LLH_LEN);
    int rlen = read(tuntap->fd, buf + UIP_LLH_LEN, len - UIP_LLH_LEN);
    if (rlen < 0) {
        return rlen;
    }
    return rlen + UIP_LLH_LEN;
}

int tuntap_write(struct tuntap_dev *tuntap, unsigned char *buf, int len) {
    uip_buf = buf;
    uip_len = len;
    if (IPBUF->ethhdr.type == htons(UIP_ETHTYPE_IP)) {
        int rlen = write(tuntap->fd, buf + UIP_LLH_LEN, len - UIP_LLH_LEN);
        if (rlen < 0) {
            return rlen;
        }
        return rlen + UIP_LLH_LEN;
    }
    return 0;
}

void tuntap_close(struct tuntap_dev *tuntap) {
    if(tuntap->fd > 0)
        close(tuntap->fd);
}

void tuntap_get_address(struct tuntap_dev *tuntap) {
}

#endif /* #ifdef __ANDROID_NDK__ */
