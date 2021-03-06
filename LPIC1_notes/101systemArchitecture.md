# LPIC-1 Training Notes

[Jump back to list of notes](../README.md)

[Link to training](https://learning.lpi.org/en/learning-materials/101-500/)

- [LPIC-1 Training Notes](#lpic-1-training-notes)
  - [101.1-Determine and Configure Hardware Settings](#1011-determine-and-configure-hardware-settings)
    - [Device Inspection](#device-inspection)
    - [Commands for Inspection](#commands-for-inspection)
  - [101.2-System Boot](#1012-system-boot)
    - [Grub Commands](#grub-commands)
  - [101.3-Change runlevels, boot targets, shutdown or reboot system](#1013-change-runlevels-boot-targets-shutdown-or-reboot-system)
    - [Runlevels](#runlevels)
    - [SystemD](#systemd)
    - [Shutdown](#shutdown)

## 101.1-Determine and Configure Hardware Settings

### Device Inspection

Check in UEFI/BIOS if something is not working correctly. If the hardware is not detected there, then the problem is almost certainly in the hardware or the port itself.
Otherwise, the issue is probably with the operating system.

### Commands for Inspection

`lspci` - Show all devices connected to the PCI bus (motherboard). Could be disk controller or expansion card (external gpu)

`lsusb` - Show all devices connected to the machine VIA USB port

Can use `-s` to show less information

Can use `-d [id]` to display detailed info about one device

## 101.2-System Boot

To access GRUB - Press `shift` while booting from BIOS, `esc` when booting from UEFI

### Grub Commands

- `acpi`: Enables/ disables ACPI support. `acpi=off` to disable support for acpi
- `init`: Sets an alternative initiator for the system. `init=/bin/bash` sets bash to the initiator so a shell session will start before OS
- `systemd.unit`: Sets systemd target to be activated. `systemd.unit=graphical.target`. To activate runlevel 1, just need `systemd.unit 1` or `systemd.unit S`
- `mem`: Sets amount of available ram. `mem=512M` limits ram to 512 MB
- `maxcpus`: limits number of cores visible. Value of 0 for non-multicore. `maxcpus=2` limites the number of processors to 2
- `quiet`: hides most boot messages
- `root`: Sets root partiton. `root=/dev/sda3`
- `ro`: makes initial mount of the root filesystem be read-only
- `rw`: allows writing in the initial boot of root filesystem

## 101.3-Change runlevels, boot targets, shutdown or reboot system

There needs to be a program to control the other programs. Historically, it was called SysVInit or Sys V. In modern distributions of linux, it is now a program called Systemd or Upstart.

### Runlevels

SysVinit is the standard now. The standard provides states, or runlevels, and their corresponding service scripts needed to run them. They range from 0 to 6 with the following common purposes:

0. System shutdown
1. Single user mode, no network or non-essential capabilities (maintenance mode)
2. Not used often. If it is, implemented like 3.
3. Multi-user mode. Can log in by console or network
4. Not used often. If it is, implemented like 3.
5. Equivalent to 3 but with a GUI login
6. System restart

The program that manages the run levels is `/sbin/init`. When the system is initialized, the `init` program identifies the requested run level (normally 5) from the kernal of `/etc/inittab`. The syntax in the `/etc/inittab` file is `id:runlevel:action:process`. The following is a list of available actions

- `Boot`: process will be executed during system initialization. The field `runlevels` is ignored.
- `bootwait`: process executed during initialization, `init` waits until it is done running to continue. `runlevels` is ignored.
- `sysinit`: Process is executed after the system initializtion. `runlevels` is ignored.
- `wait`: process will be executed for the runlevels and `init` waits until it is finished to continue
- `respawn`: the process is restarted if terminated
- `ctrlaltdel`: process is executed when the init process receives the `SIGINT` signal, from `ctrl`+`alt`+`delete` being pressed

The default runlevel is also defined in `/etc/inittab` in the form of `id:x:initdefault` where x is the number of the desired default run level. It should never be 0 or 6 as that woulc cause the system to immediately shut down.
Everytime the `/etc/inittab` file is modified, run `telinit q` to reload it's configuration with the modified file. Otherwise. it is possible that there will be a system halt.  
Scripts used by `init` are stored in the `/etc/init.d` directory, with each runlevel having its own directory in `/etc/`, named `/etc/rc0.d/`, `/etc/rc1.d/`, and so on. The scripts for each runlevel should be stored in the relevant directory.

### SystemD

SystemD is the most widely used ste of tools to manage system resources, referred to as units. For example, the unit for a httpd server process (such as apache) is under the unit `httpd.service`.
There are seven distinct kinds of units:

- `service`: most common unit. For active system resources that can be initiated, interrupted, and reloaded
- `socket`: Can be a filesystem socket or network socket. Unit gets loaded when the socket receives a request
- `device`: Associated with a hardware devices identified by the kernal.
- `mount`: Mount point definition in the filesystem, similar to an entry in `/etc/fstab`
- `automount`: similar to a `mount` unit, but it is mounted automatically. Every `automount` unit has an associated `mount`.
- `target`: Target unit is a grouping of other units to be managed as a single unit
- `snapshot`: a saved state of the systemd manager (this isn't available on every distro)

There are also multiple commands for controlling these. For all of these, if there is not another unit with the same name but a different type, you do not need to specify what is after the dot in `unit.service`. The main command is systemctl.

- `systemctl start unit.service`: starts the unit
- `systemctl stop unit.service`: stops the unit
- `systemctl restart unit.service`: restarts the unit
- `systemctl status unit.service`: shows the status of the unit, including if it is running or not
- `systemctl is-active unit.service`: shows 'active' if the unit is running, 'inactive' if otherwise
- `systemctl enable unit.service`: enables a unit to load during system initialization
- `systemctl disable unit.service`: disables a unit from running at system initialization
- `systemctl is-enabled unit.service`: Stores the answer in the variable `$?`. Value of 0 indicates that it does start with the system,1 for it does not start with system

### Shutdown

When `shutdown` is run, all processed receive the signals `SIGTERM` and `SIGKILL`. Then the system either shuts down or changes its runlevel. By default, it will move the system to runlevel 1. To change this default, use the command `$ shutdown [option] time [message]`. Only the parameter time is required, it can be entered in several forms.

- `hh:mm`
- `+m`: Specifies number of minutes to wait
- `now` or `+0`: immediate execution

The `message` field is the message sent to all users before shutting down.
