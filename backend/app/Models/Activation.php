<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Activation extends Model
{
    protected $fillable = [
        'client_id',
        'mobile_number',
        'start_date',
        'expiry_date',
        'status',
        'whatsapp_enabled',
        'whatsapp_message',
        'whatsapp_attachment',
        'attachment_type',
        'sms_enabled',
        'sms_message',
        'remarks',
        'device_id',
'device_name',
'app_version',
'last_seen',
'blocked',
    ];

    protected $casts = [
        'whatsapp_enabled' => 'boolean',
        'sms_enabled' => 'boolean',
        'start_date' => 'date',
        'expiry_date' => 'date',
        'last_seen' => 'datetime',
'blocked' => 'boolean',
    ];

    public function client(): BelongsTo
    {
        return $this->belongsTo(Client::class);
    }
}