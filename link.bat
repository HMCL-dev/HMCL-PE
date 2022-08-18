@echo off

call:chklink n2n_v1 bundles\n2n_meyerd\n2n_v1
call:chklink n2n_v2 bundles\n2n_ntop_v2
call:chklink n2n_v2s bundles\n2n_meyerd\n2n_v2
call:chklink n2n_v3 bundles\n2n_ntop_v3
call:chklink uip bundles\uip\uip
call:chklink tun2tap bundles\tun2tap
call:chklink slog bundles\slog
pushd Hin2n\src\main\cpp
call:chklink n2n_v1 ..\..\..\..\bundles\n2n_meyerd\n2n_v1
call:chklink n2n_v2 ..\..\..\..\bundles\n2n_ntop_v2
call:chklink n2n_v2s ..\..\..\..\bundles\n2n_meyerd\n2n_v2
call:chklink n2n_v3 ..\..\..\..\bundles\n2n_ntop_v3
call:chklink uip ..\..\..\..\bundles\uip\uip
call:chklink tun2tap ..\..\..\..\bundles\tun2tap
call:chklink slog ..\..\..\..\bundles\slog
popd
goto:eof

:chklink
if exist %~1 (
	pushd %~1 2>nul && popd || (del /q %~1 & mklink /d %~1 %~2)
) else (
	mklink /d %~1 %~2
)
goto:eof