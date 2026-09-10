<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Device extends Model
{
    protected $fillable = [

        'license_id',
        'device_uid',
        'device_name',
        'computer_name',
        'os_name',
        'os_version',
        'ip_address',
        'mac_address',
        'last_seen',
        'status'

    ];

    public function license()
    {
        return $this->belongsTo(License::class);
    }
}