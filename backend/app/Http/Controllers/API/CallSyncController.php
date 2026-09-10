<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\Activation;
use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\File;
use Illuminate\Support\Facades\Storage;

class CallSyncController extends Controller
{
    public function sync(Request $request)
    {
        /*
        |--------------------------------------------------------------------------
        | API Key
        |--------------------------------------------------------------------------
        */

        if ($request->api_key !== config('replypro.api_key')) {

            return response()->json([
                'status' => false,
                'message' => 'UNAUTHORIZED'
            ], 401);
        }

        /*
        |--------------------------------------------------------------------------
        | Validate
        |--------------------------------------------------------------------------
        */

        $request->validate([

            'mobile_number' => 'required|string',

            'contact_name' => 'nullable|string',

            'call_type' => 'required|string',

            'duration' => 'required|numeric',

            'call_date' => 'required',

            'device_id' => 'required|string',

        ]);

        /*
        |--------------------------------------------------------------------------
        | Find Activation By Device
        |--------------------------------------------------------------------------
        */

        $activation = Activation::with('client')
            ->where(
                'device_id',
                trim($request->device_id)
            )
            ->first();

        if (!$activation) {

            return response()->json([
                'status' => false,
                'message' => 'Device not activated.'
            ], 404);
        }

        /*
        |--------------------------------------------------------------------------
        | Blocked
        |--------------------------------------------------------------------------
        */

        if ($activation->blocked) {

            return response()->json([
                'status' => false,
                'message' => 'License blocked.'
            ], 403);
        }

        /*
        |--------------------------------------------------------------------------
        | Expired
        |--------------------------------------------------------------------------
        */

        if (
            Carbon::parse(
                $activation->expiry_date
            )->isPast()
        ) {

            $activation->update([
                'status' => 'Expired'
            ]);

            return response()->json([
                'status' => false,
                'message' => 'License expired.'
            ], 403);
        }

        /*
        |--------------------------------------------------------------------------
        | Update Last Seen
        |--------------------------------------------------------------------------
        */

        $activation->update([

            'last_seen' => now(),

            'status' => 'Active',

        ]);

        /*
        |--------------------------------------------------------------------------
        | Attachment URL
        |--------------------------------------------------------------------------
        */

        $attachmentUrl = null;

        $attachmentPath =
            trim(
                (string) $activation->whatsapp_attachment
            );

        if (
            $attachmentPath !== ''
        ) {

            /*
             * Remove leading slash so all formats become:
             *
             * attachments/file.png
             *
             * /attachments/file.png
             *
             * storage/attachments/file.png
             */

            $cleanPath =
                ltrim(
                    $attachmentPath,
                    '/'
                );

            /*
             |--------------------------------------------------------------------------
             | CASE 1
             |
             | File stored in:
             |
             | storage/app/public/attachments/
             |
             |--------------------------------------------------------------------------
             */

            $storagePath =
                str_starts_with(
                    $cleanPath,
                    'storage/'
                )
                    ? substr(
                        $cleanPath,
                        strlen('storage/')
                    )
                    : $cleanPath;

            if (
                Storage::disk('public')
                    ->exists($storagePath)
            ) {

                $attachmentUrl =
                    Storage::disk('public')
                        ->url($storagePath);

            }

            /*
             |--------------------------------------------------------------------------
             | CASE 2
             |
             | File stored directly in:
             |
             | public/attachments/
             |
             |--------------------------------------------------------------------------
             */

            if (
                !$attachmentUrl
            ) {

                $publicPath =
                    public_path(
                        $cleanPath
                    );

                if (
                    File::exists(
                        $publicPath
                    )
                ) {

                    $attachmentUrl =
                        asset(
                            $cleanPath
                        );
                }
            }

            /*
             |--------------------------------------------------------------------------
             | CASE 3
             |
             | Existing database value already contains
             | a complete URL.
             |--------------------------------------------------------------------------
             */

            if (
                !$attachmentUrl &&
                (
                    str_starts_with(
                        $cleanPath,
                        'http://'
                    ) ||
                    str_starts_with(
                        $cleanPath,
                        'https://'
                    )
                )
            ) {

                $attachmentUrl =
                    $cleanPath;
            }

            /*
             |--------------------------------------------------------------------------
             | CASE 4
             |
             | Fallback.
             |
             | This keeps compatibility with the existing
             | attachments/filename format.
             |--------------------------------------------------------------------------
             */

            if (
                !$attachmentUrl
            ) {

                $attachmentUrl =
                    asset(
                        $cleanPath
                    );
            }
        }

        /*
        |--------------------------------------------------------------------------
        | Response
        |--------------------------------------------------------------------------
        */

        return response()->json([

            'status' => true,

            'customer' => [

                'mobile' =>
                    $request->mobile_number,

                'name' =>
                    $request->contact_name,

            ],

            'actions' => [

                'send_whatsapp' =>
                    (bool) $activation->whatsapp_enabled,

                'send_sms' =>
                    (bool) $activation->sms_enabled,

            ],

            'messages' => [

                'whatsapp' =>
                    $activation->whatsapp_message,

                'sms' =>
                    $activation->sms_message,

            ],

            'attachment' => [

                'type' =>
                    $activation->attachment_type,

                'url' =>
                    $attachmentUrl,

            ],

            'activation' => [

                'mobile' =>
                    $activation->mobile_number,

                'expiry' =>
                    $activation->expiry_date,

                'client' =>
                    optional(
                        $activation->client
                    )->name,

            ]

        ]);
    }
}