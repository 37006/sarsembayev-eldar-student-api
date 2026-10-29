#!/usr/bin/env bash
set -euo pipefail
project_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
mkdir -p "$project_dir/logs"
{
    printf 'Student: Eldar Sarsembayev\nGroup: IT2-2301\nStudent ID: 37006\n'
    printf '\nDate and time: '; TZ=Asia/Qyzylorda date --iso-8601=seconds
    printf 'Username: '; whoami
    printf '\nOperating system\n'; cat /etc/os-release
    printf '\nKernel\n'; uname -srmo
    printf '\nCPU\n'; lscpu
    printf '\nMemory\n'; free -h
    printf '\nDisk usage and available space\n'; df -h
} | tee "$project_dir/logs/Sarsembayev_Eldar_system_report.log"
