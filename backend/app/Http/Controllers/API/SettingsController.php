<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\Activation;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class SettingsController extends Controller
{

    //--------------------------------------------------
    // GET SETTINGS
    //--------------------------------------------------

    public function get($mobile)
    {

      $activation = Activation::where(

    'mobile_number',

    $mobile

)->first();

        if (!$activation) {

            return response()->json([

                'status' => false,

                'message' => 'Activation not found'

            ], 404);

        }

        return response()->json([

            'status' => true,

            'settings' => [

                'whatsapp_enabled'   => (bool) $activation->whatsapp_enabled,

                'whatsapp_message'   => $activation->whatsapp_message,

                'whatsapp_attachment'=> $activation->whatsapp_attachment,

                'attachment_type'    => $activation->attachment_type,

                'sms_enabled'        => (bool) $activation->sms_enabled,

                'sms_message'        => $activation->sms_message

            ]

        ]);

    }

    //--------------------------------------------------
    // UPDATE SETTINGS
    //--------------------------------------------------

    public function update(Request $request)
    {

        $validator = Validator::make($request->all(), [

            'mobile_number'      => 'required',

            'whatsapp_enabled'   => 'required|boolean',

            'whatsapp_message'   => 'nullable|string',

            'sms_enabled'        => 'required|boolean',

            'sms_message'        => 'nullable|string'

        ]);

        if ($validator->fails()) {

            return response()->json([

                'status' => false,

                'message' => $validator->errors()->first()

            ], 422);

        }

        $activation = Activation::where(

            'mobile_number',

            $request->mobile_number

        )->first();

        if (!$activation) {

            return response()->json([

                'status' => false,

                'message' => 'Activation not found'

            ], 404);

        }

        $activation->update([

            'whatsapp_enabled' => $request->whatsapp_enabled,

            'whatsapp_message' => $request->whatsapp_message,

            'sms_enabled'      => $request->sms_enabled,

            'sms_message'      => $request->sms_message

        ]);

        return response()->json([

            'status' => true,

            'message' => 'Settings updated successfully'

        ]);

    }

    //--------------------------------------------------
    // UPLOAD ATTACHMENT
    //--------------------------------------------------

    public function upload(Request $request)
    {

        $validator = Validator::make($request->all(), [

            'mobile_number' => 'required',

            'attachment' => 'required|file|max:51200'

        ]);

        if ($validator->fails()) {

            return response()->json([

                'status' => false,

                'message' => $validator->errors()->first()

            ], 422);

        }

        $activation = Activation::where(

            'mobile_number',

            $request->mobile_number

        )->first();

        if (!$activation) {

            return response()->json([

                'status' => false,

                'message' => 'Activation not found'

            ], 404);

        }

        $file = $request->file('attachment');

        $name = time() . "_" . $file->getClientOriginalName();

        $file->move(

            public_path('attachments'),

            $name

        );

        $extension = strtolower(

            $file->getClientOriginalExtension()

        );

        $type = 'image';

        if ($extension == 'pdf') {

            $type = 'pdf';

        } elseif (

            in_array($extension, [

                'mp4',

                'avi',

                'mov',

                'mkv'

            ])

        ) {

            $type = 'video';

        }

     $activation->update([
    'whatsapp_attachment' => asset('attachments/' . $name),
    'attachment_type' => $type
]);

        return response()->json([

            'status' => true,

            'message' => 'Attachment uploaded successfully',

            'attachment' => $name,

            'attachment_type' => $type

        ]);

    }

}